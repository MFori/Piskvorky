package cz.martinforejt.piskvorky.server.features.socket

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.toSnap
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.core.service.SocketBroadcaster
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.core.service.SocketServiceSession
import cz.martinforejt.piskvorky.server.features.game.usecase.GiveUpGameUseCase
import cz.martinforejt.piskvorky.server.security.JwtManager
import io.ktor.http.cio.websocket.*
import org.koin.core.inject

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class SocketServiceSessionImpl(
    sessionId: String,
    broadcaster: SocketBroadcaster,
    private val connection: WebSocketSession
) : SocketServiceSession(sessionId, broadcaster) {

    private val usersRepository by inject<UsersRepository>()
    private val friendsRepository by inject<FriendsRepository>()
    private val gameRepository by inject<GameRepository>()
    private val jwtManager by inject<JwtManager>()
    private val socketService by inject<SocketService>()
    private val giveUpGameUseCase by inject<GiveUpGameUseCase>()

    @Throws(SocketApiException::class)
    override suspend fun receivedMessage(data: String) {
        val message = SocketApi.decode(data)
        if (!authorized && message.action != SocketApiAction.AUTHORIZE) {
            throw UnauthorizedSocketException()
        }

        when (message.action) {
            SocketApiAction.AUTHORIZE -> authorize(message.data as AuthorizeSocketApiMessage)
            SocketApiAction.ONLINE_USERS -> onlineUsers()
            SocketApiAction.FRIENDS -> friends()
            SocketApiAction.CHAT_MESSAGE -> chatMessage(message.data as ChatMessageSocketApiMessage)
            SocketApiAction.ERROR -> errorMessage()
            else -> {
                throw InvalidSocketMessageException()
            }
        }
    }

    override suspend fun userLeft() {
        setUserOnline(false)
        user?.let {
            giveUpGameUseCase.execute(GiveUpGameUseCase.Params(it))
        }

    }

    private suspend fun authorize(message: AuthorizeSocketApiMessage) {
        val principal = jwtManager.validateToken(message.token)
        if (principal != null) {
            if (socketService.isOnline(principal.id, sessionId)) {
                // already connected in other device
                send(
                    SocketApi.encode(
                        SocketApiAction.ERROR,
                        Error(SocketApiCode.ALREADY_CONNECTED.value, "Already connected")
                    )
                )
                connection.close()
                return
            }
            user = principal
            send(SocketApi.encode(SocketApiAction.AUTHORIZE, Error(SocketApiCode.OK.value, "Successfully authorized.")))
            setUserOnline(true)

            gameRepository.getGame(principal.id)?.let {
                socketService.sendMessageTo(principal.id, GameUpdateSocketApiMessage(it.toSnap()))
            }
        } else {
            user = null
            send(SocketApi.encode(SocketApiAction.AUTHORIZE, Error(SocketApiCode.UNAUTHORIZED.value, "Invalid token.")))
        }
    }


    private suspend fun setUserOnline(@Suppress("UNUSED_PARAMETER") online: Boolean) {
        user?.let {
            val message = SocketApi.encode(onlineUsersMessage())
            broadcaster.sendBroadcast(message)
        }
    }

    private suspend fun onlineUsers() {
        send(SocketApi.encode(onlineUsersMessage()))
    }

    private suspend fun friends() {
        val users = friendsRepository.getFriends(user!!.id)
        send(SocketApi.encode(FriendsSocketApiMessage(users)))
    }

    private suspend fun chatMessage(message: ChatMessageSocketApiMessage) {
        val game = user?.let { gameRepository.getGame(it.id) } ?: return
        broadcaster.sendMessageTo(game.rivalPlayer(user!!.id).id, message)
    }

    private fun errorMessage() {
        throw InvalidSocketMessageException()
    }

    override suspend fun send(message: String) = connection.send(Frame.Text(message))

    override suspend fun send(message: SocketApiMessage<SocketApiMessageData>) = send(SocketApi.encode(message))

    private suspend fun onlineUsersMessage(): OnlineUsersSocketApiMessage {
        val users = usersRepository.getOnlineUsers()
        return OnlineUsersSocketApiMessage(users)
    }

}