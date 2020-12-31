package cz.martinforejt.piskvorky.server.features.lobby

import io.ktor.http.cio.websocket.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class LobbyServiceImpl : LobbyService {

    private val sessions = ConcurrentHashMap<String, MutableList<LobbySession>>()

    override fun newConnection(sessionId: String, connection: WebSocketSession): LobbySession {
        val session = LobbySessionFactory.getSession(sessionId, this, connection)

        val list = sessions.computeIfAbsent(sessionId) { CopyOnWriteArrayList() }
        list.add(session)

        return session
    }

    override suspend fun receivedMessage(data: String, session: LobbySession) {
        session.receivedMessage(data)
    }

    override suspend fun send(message: String, session: LobbySession) {
        send(message, session.sessionId)
    }

    override suspend fun send(message: String, sessionId: String) {
        sessions[sessionId]?.forEach {
            println("send now lobbysession")
            it.send(message) }
    }

    override suspend fun send(message: String, vararg sessionIds: String) {
        sessionIds.forEach { sessionId ->
            send(message, sessionId)
        }
    }

    override suspend fun sendBroadcast(message: String) {
        println("send broadcast $message")
        sessions.keys.forEach { sessionId ->
            println("send now $sessionId")
            send(message, sessionId)
        }
    }

    override suspend fun sendOthers(message: String, sessionId: String) {
        sessions.keys.forEach { session ->
            if (session != sessionId) {
                send(message, session)
            }
        }
    }

    override suspend fun disconnect(session: LobbySession) {
        val connections = sessions[session.sessionId]
        connections?.remove(session)

        if (connections != null && connections.isEmpty()) {
            sessions.remove(session.sessionId)
            session.userLeft()
        }
    }
}