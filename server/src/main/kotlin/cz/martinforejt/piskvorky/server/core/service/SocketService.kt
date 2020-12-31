package cz.martinforejt.piskvorky.server.core.service

import cz.martinforejt.piskvorky.api.model.SocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApiMessageData
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.http.cio.websocket.*
import org.koin.core.KoinComponent

/**
 * Created by Martin Forejt on 31.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface ISocketService : SocketBroadcaster {

    fun newConnection(sessionId: String, connection: WebSocketSession): SocketServiceSession

    suspend fun disconnect(session: SocketServiceSession)

    suspend fun receivedMessage(data: String, session: SocketServiceSession)

    suspend fun send(message: String, session: SocketServiceSession)

    suspend fun send(message: String, sessionId: String)

    suspend fun send(message: String, vararg sessionIds: String)

}

interface SocketBroadcaster {

    suspend fun sendBroadcast(message: String)

    suspend fun sendBroadcast(message: String, channel: String)

    suspend fun sendBroadcastAllChannels(message: String)

    suspend fun sendOthers(message: String, exceptSessionId: String)

    suspend fun sendOthers(message: String, channel: String, exceptSessionId: String)

    suspend fun sendOthersAllChannels(message: String, exceptSessionId: String)
}

interface SocketServicesManager {

    fun getService(channel: String): ISocketService

    fun channels(): List<String>
}

abstract class SocketService(
    val channel: String,
    private val socketServicesManager: SocketServicesManager
) : ISocketService {

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
        sendBroadcast(message, channel)
    }

    override suspend fun sendBroadcast(message: String, channel: String) {
        sessions.keys.forEach { sessionId ->
            send(message, sessionId)
        }
    }

    override suspend fun sendBroadcastAllChannels(message: String) {
        socketServicesManager.channels().forEach { channel ->
            sendBroadcast(message, channel)
        }
    }

    override suspend fun sendOthers(message: String, exceptSessionId: String) {
        sendOthers(message, channel, exceptSessionId)
    }

    override suspend fun sendOthers(message: String, channel: String, exceptSessionId: String) {
        sessions.keys.forEach { sessionId ->
            if (sessionId != exceptSessionId) {
                send(message, sessionId)
            }
        }
    }

    override suspend fun sendOthersAllChannels(message: String, exceptSessionId: String) {
        socketServicesManager.channels().forEach { channel ->
            sendOthers(message, channel, exceptSessionId)
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

abstract class SocketServiceSession(
    val sessionId: String,
    val channel: String,
    val broadcaster: SocketBroadcaster
) : KoinComponent {

    var user: UserPrincipal? = null
    val authorized
        get() = user != null

    abstract suspend fun userLeft()

    abstract suspend fun receivedMessage(data: String)

    abstract suspend fun send(message: String)

    abstract suspend fun send(message: SocketApiMessage<SocketApiMessageData>)
}