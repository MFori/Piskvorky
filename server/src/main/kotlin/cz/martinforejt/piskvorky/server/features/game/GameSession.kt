package cz.martinforejt.piskvorky.server.features.game

import cz.martinforejt.piskvorky.api.model.SocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApiMessageData
import cz.martinforejt.piskvorky.server.core.service.SocketBroadcaster
import io.ktor.http.cio.websocket.*

/**
 * Created by Martin Forejt on 31.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class GameSessionImpl(
    sessionId: String,
    channel: String,
    broadcaster: SocketBroadcaster,
    private val connection: WebSocketSession
) : GameSession(sessionId, channel, broadcaster) {

    override suspend fun userLeft() {
        TODO("Not yet implemented")
    }

    override suspend fun receivedMessage(data: String) {
        TODO("Not yet implemented")
    }

    override suspend fun send(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun send(message: SocketApiMessage<SocketApiMessageData>) {
        TODO("Not yet implemented")
    }
}