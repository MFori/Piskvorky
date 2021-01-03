package cz.martinforejt.piskvorky.server.features.lobby

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.core.service.SocketBroadcaster
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager
import cz.martinforejt.piskvorky.server.security.JwtManager
import io.ktor.http.cio.websocket.*
import org.koin.core.inject

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class LobbySessionImpl(
    sessionId: String,
    channel: String,
    broadcaster: SocketBroadcaster,
    private val connection: WebSocketSession
) : LobbySession(sessionId, channel, broadcaster) {

    private val usersRepository by inject<UsersRepository>()
    private val friendsRepository by inject<FriendsRepository>()
    private val jwtManager by inject<JwtManager>()
    private val socketServicesManager by inject<SocketServicesManager>()

    @Throws(SocketApiException::class)
    override suspend fun receivedMessage(data: String) {
        println("received $data")
        val message = SocketApi.decode(data)
        if (!authorized && message.action != SocketApiAction.AUTHORIZE) {
            throw UnauthorizedSocketException()
        }

        when (message.action) {
            SocketApiAction.AUTHORIZE -> authorize(message.data as AuthorizeSocketApiMessage)
            SocketApiAction.ONLINE_USERS -> onlineUsers()
            SocketApiAction.FRIENDS -> friends()
            SocketApiAction.ERROR -> errorMessage()
            else -> {
                throw InvalidSocketMessageException()
            }
        }
    }

    override suspend fun userLeft() {
        setUserOnline(false)
    }

    private suspend fun authorize(message: AuthorizeSocketApiMessage) {
        val principal = jwtManager.validateToken(message.token)
        if (principal != null) {
            if (socketServicesManager.isOnline(principal.id, sessionId)) {
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
        } else {
            user = null
            send(SocketApi.encode(SocketApiAction.AUTHORIZE, Error(SocketApiCode.UNAUTHORIZED.value, "Invalid token.")))
        }
    }


    private suspend fun setUserOnline(online: Boolean) {
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