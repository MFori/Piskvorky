package cz.martinforejt.piskvorky.server.core.service

import cz.martinforejt.piskvorky.server.features.game.GameServiceImpl
import cz.martinforejt.piskvorky.server.features.game.GameSessionImpl
import cz.martinforejt.piskvorky.server.features.lobby.LobbyServiceImpl
import cz.martinforejt.piskvorky.server.features.lobby.LobbySessionImpl
import io.ktor.http.cio.websocket.*

/**
 * Created by Martin Forejt on 31.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class SocketServicesManagerImpl : SocketServicesManager {

    private val services = mutableMapOf<String, ISocketService>()

    override fun getService(channel: String): ISocketService {
        if (!services.containsKey(channel)) {
            services[channel] = SocketServiceFactory.getService(channel, this)
        }

        return services[channel]!!
    }

    override fun channels(): List<String> {
        return services.keys.toList()
    }
}

object SocketServiceFactory {

    fun getService(channel: String, manager: SocketServicesManager): ISocketService {
        return when (channel) {
            "lobby" -> {
                LobbyServiceImpl(manager)
            }
            "game" -> {
                GameServiceImpl(manager)
            }
            else -> {
                throw IllegalArgumentException("Invalid channel name: $channel")
            }
        }
    }

}

object SocketServiceSessionFactory {
    fun getSession(
        sessionId: String,
        channel: String,
        broadcaster: SocketBroadcaster,
        connection: WebSocketSession
    ): SocketServiceSession {
        return when (channel) {
            "lobby" -> {
                LobbySessionImpl(sessionId, channel, broadcaster, connection)
            }
            "game" -> {
                GameSessionImpl(sessionId, channel, broadcaster, connection)
            }
            else -> {
                throw IllegalArgumentException("Invalid channel name: $channel")
            }
        }
    }
}