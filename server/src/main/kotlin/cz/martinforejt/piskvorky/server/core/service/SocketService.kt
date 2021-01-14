package cz.martinforejt.piskvorky.server.core.service

import cz.martinforejt.piskvorky.api.model.SocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApiMessageData
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.http.cio.websocket.*
import org.koin.core.KoinComponent

/**
 * Created by Martin Forejt on 31.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/**
 * Socket service, manage all socket connections
 */
interface ISocketService : SocketBroadcaster {

    /**
     * Create new session
     *
     * @param sessionId session id, session can be any string representing current session
     * @param connection websocket connection
     */
    fun newConnection(sessionId: String, connection: WebSocketSession): SocketServiceSession

    /**
     * Disconnect session
     */
    suspend fun disconnect(session: SocketServiceSession)

    /**
     * On received message by [session]
     */
    suspend fun receivedMessage(data: String, session: SocketServiceSession)

    /**
     * Send message to [session]
     */
    suspend fun send(message: String, session: SocketServiceSession)

    /**
     * Send message to session with [sessionId]
     */
    suspend fun send(message: String, sessionId: String)

    /**
     * Send message to all session with [sessionIds]
     */
    suspend fun send(message: String, vararg sessionIds: String)

    /**
     * Get list of online users (users connected via websocket to this service)
     */
    suspend fun getOnlineUsers(): List<PublicUser>

    /**
     * Check if user with [userId] is online
     *
     * @param sessionId optionally search user only within session with this id
     */
    fun isOnline(userId: Int, sessionId: String? = null): Boolean
}

/**
 * Broadcaster can send messages to specific users or groups
 */
interface SocketBroadcaster {

    /**
     * Send message to all connections
     */
    suspend fun sendBroadcast(message: String)

    /**
     * Send message to all connections except session with [exceptSessionId]
     */
    suspend fun sendOthers(message: String, exceptSessionId: String)

    /**
     * Send message to user with [userId]
     */
    suspend fun sendMessageTo(userId: Int, message: String)

    /**
     * Send message to user with [userId]
     */
    suspend fun sendMessageTo(userId: Int, message: SocketApiMessageData)

}

/**
 * Abstract socket service
 */
abstract class SocketService : ISocketService {

    /**
     * Map of sessions
     *      key = session id
     *      value = list of socket sessions
     */
    abstract val sessions: MutableMap<String, MutableList<SocketServiceSession>>

    override suspend fun receivedMessage(data: String, session: SocketServiceSession) {
        session.receivedMessage(data)
    }

    override suspend fun send(message: String, session: SocketServiceSession) {
        send(message, session.sessionId)
    }

    override suspend fun send(message: String, vararg sessionIds: String) {
        sessionIds.forEach { sessionId ->
            send(message, sessionId)
        }
    }

    override suspend fun send(message: String, sessionId: String) {
        sessions[sessionId]?.forEach { session ->
            session.send(message)
        }
    }

    override suspend fun sendBroadcast(message: String) {
        sessions.keys.forEach { sessionId ->
            send(message, sessionId)
        }
    }

    override suspend fun sendOthers(message: String, exceptSessionId: String) {
        sessions.keys.forEach { sessionId ->
            if (sessionId != exceptSessionId) {
                send(message, sessionId)
            }
        }
    }

    override suspend fun disconnect(session: SocketServiceSession) {
        val connections = sessions[session.sessionId]
        connections?.remove(session)

        if (connections != null && connections.isEmpty()) {
            sessions.remove(session.sessionId)
            session.userLeft()
        }
    }
}

/**
 * Session represents one socket connection
 *
 * @property sessionId
 * @property broadcaster
 */
abstract class SocketServiceSession(
    val sessionId: String,
    val broadcaster: SocketBroadcaster
) : KoinComponent {

    var user: UserPrincipal? = null
    val authorized
        get() = user != null

    /**
     * Called on user left (from all connection within this [sessionId]
     */
    abstract suspend fun userLeft()

    /**
     * Received message
     */
    abstract suspend fun receivedMessage(data: String)

    /**
     * Send message to this session
     */
    abstract suspend fun send(message: String)

    /**
     * Send message to this session
     */
    abstract suspend fun send(message: SocketApiMessage<SocketApiMessageData>)
}