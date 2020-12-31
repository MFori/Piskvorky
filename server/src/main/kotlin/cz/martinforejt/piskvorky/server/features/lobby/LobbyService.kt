package cz.martinforejt.piskvorky.server.features.lobby

import cz.martinforejt.piskvorky.api.model.SocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApiMessageData
import io.ktor.http.cio.websocket.*

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface LobbyService : LobbySendManager {

    fun newConnection(sessionId: String, connection: WebSocketSession): LobbySession

    suspend fun disconnect(session: LobbySession)

    suspend fun receivedMessage(data: String, session: LobbySession)

    suspend fun send(message: String, session: LobbySession)

    suspend fun send(message: String, sessionId: String)

    suspend fun send(message: String, vararg sessionIds: String)

}

interface LobbySendManager {

    suspend fun sendBroadcast(message: String)

    suspend fun sendOthers(message: String, sessionId: String)

}

abstract class LobbySession(
    val sessionId: String,
    val sendManager: LobbySendManager
) {

    abstract suspend fun userLeft()

    abstract suspend fun receivedMessage(data: String)

    abstract suspend fun send(message: String)

    abstract suspend fun send(message: SocketApiMessage<SocketApiMessageData>)

}