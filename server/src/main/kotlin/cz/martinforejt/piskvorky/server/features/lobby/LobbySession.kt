package cz.martinforejt.piskvorky.server.features.lobby

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.security.JwtManager
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.http.cio.websocket.*
import kotlin.jvm.Throws

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class LobbySession(
    private val sessionId: String,
    private val connection: WebSocketSession,
    private val jwtManager: JwtManager,
    private val usersRepository: UsersRepository
) {
    var user: UserPrincipal? = null
        private set
    private val authorized
        get() = user != null

    @Throws(SocketApiException::class)
    suspend fun receivedMessage(data: String) {
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

    suspend fun left() {
        user?.let { usersRepository.setOnline(PublicUser(it.id, it.email, true), false) }
    }

    private suspend fun authorize(message: AuthorizeSocketApiMessage) {
        val principal = jwtManager.validateToken(message.token)
        if (principal != null) {
            user = principal
            usersRepository.setOnline(PublicUser(principal.id, principal.email, true), true)
            send(SocketApi.encode(SocketApiAction.AUTHORIZE, Error(SocketApiCode.OK.value, "Successfully authorized.")))
        } else {
            user = null
            send(SocketApi.encode(SocketApiAction.AUTHORIZE, Error(SocketApiCode.UNAUTHORIZED.value, "Invalid token.")))
        }
    }

    private suspend fun onlineUsers() {
        val users = usersRepository.getOnlineUsers()
        send(SocketApi.encode(OnlineUsersSocketApiMessage(users)))
    }

    private suspend fun friends() {
        val users = usersRepository.getFriends(user!!.id)
        send(SocketApi.encode(FriendsSocketApiMessage(users)))
    }

    private fun errorMessage() {
        throw InvalidSocketMessageException()
    }

    private suspend fun send(message: String) = connection.send(Frame.Text(message))
    private suspend fun send(message: SocketApiMessage<SocketApiMessageData>) = send(SocketApi.encode(message))
}