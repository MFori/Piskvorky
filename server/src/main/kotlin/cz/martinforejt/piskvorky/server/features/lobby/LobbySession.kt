package cz.martinforejt.piskvorky.server.features.lobby

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.security.JwtManager
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.http.cio.websocket.*
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object LobbySessionFactory {
    fun getSession(sessionId: String, lobbySendManager: LobbySendManager, connection: WebSocketSession): LobbySession {
        return LobbySessionImpl(sessionId, lobbySendManager, connection)
    }
}

class LobbySessionImpl(
    sessionId: String,
    sendManager: LobbySendManager,
    private val connection: WebSocketSession
) : LobbySession(sessionId, sendManager), KoinComponent {

    private val usersRepository by inject<UsersRepository>()
    private val friendsRepository by inject<FriendsRepository>()
    private val jwtManager by inject<JwtManager>()

    private var user: UserPrincipal? = null
    private val authorized
        get() = user != null

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
            usersRepository.setOnline(PublicUser(it.id, it.email, online), online)
            val message = SocketApi.encode(onlineUsersMessage())
            sendManager.sendBroadcast(message)
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