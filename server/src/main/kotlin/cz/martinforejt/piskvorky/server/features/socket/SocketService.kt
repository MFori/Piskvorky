package cz.martinforejt.piskvorky.server.features.socket

import cz.martinforejt.piskvorky.api.model.SocketApi
import cz.martinforejt.piskvorky.api.model.SocketApiMessageData
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.core.service.SocketServiceSession
import io.ktor.http.cio.websocket.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class SocketServiceImpl(
    private val gameRepository: GameRepository
) : SocketService() {

    override val sessions: MutableMap<String, MutableList<SocketServiceSession>> =
        ConcurrentHashMap<String, MutableList<SocketServiceSession>>()

    override fun newConnection(sessionId: String, connection: WebSocketSession): SocketServiceSession {
        val session = SocketServiceSessionImpl(sessionId, this, connection)

        val list = sessions.computeIfAbsent(sessionId) { CopyOnWriteArrayList() }
        list.add(session)

        return session
    }

    override suspend fun getOnlineUsers(): List<PublicUser> {
        val users = mutableListOf<PublicUser>()
        sessions.forEach { (_, sessions) ->
            sessions.firstOrNull()?.user?.let {
                users.add(PublicUser(it.id, it.email, online = true, inGame = gameRepository.getGame(it.id) != null))
            }
        }
        return users
    }

    override fun isOnline(userId: Int, sessionId: String?): Boolean {
        sessions.forEach { (sId, sessions) ->
            if (sId != sessionId) {
                sessions.firstOrNull()?.user?.let {
                    if (it.id == userId) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override suspend fun sendMessageTo(userId: Int, message: String) {
        sessions.forEach { (_, sessions) ->
            if (sessions.firstOrNull()?.user?.id == userId) {
                sessions.forEach { it.send(message) }
            }
        }
    }

    override suspend fun sendMessageTo(userId: Int, message: SocketApiMessageData) {
        sendMessageTo(userId, SocketApi.encode(message))
    }
}