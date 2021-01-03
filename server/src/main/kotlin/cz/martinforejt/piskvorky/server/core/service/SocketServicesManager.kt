package cz.martinforejt.piskvorky.server.core.service

import cz.martinforejt.piskvorky.api.model.SocketApi
import cz.martinforejt.piskvorky.api.model.SocketApiMessageData
import cz.martinforejt.piskvorky.domain.model.PublicUser
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

    private val services = mutableMapOf<String, SocketService>()

    override fun getService(channel: String): SocketService {
        if (!services.containsKey(channel)) {
            services[channel] = SocketServiceFactory.getService(channel, this)
        }

        return services[channel]!!
    }

    override fun channels(): List<String> {
        return services.keys.toList()
    }

    override fun getOnlineUsers(): List<PublicUser> {
        val users = mutableListOf<PublicUser>()
        services.forEach { (channel, service) ->
            service.sessions.forEach { (_, sessions) ->
                sessions.firstOrNull()?.user?.let {
                    users.add(PublicUser(it.id, it.email, online = true, inGame = channel == "game"))
                }
            }
        }
        return users
    }

    override fun isOnline(userId: Int, sessionId: String?): Boolean {
        services.forEach { (_, service) ->
            service.sessions.forEach { (sId, sessions) ->
                if (sId != sessionId) {
                    sessions.firstOrNull()?.user?.let {
                        if (it.id == userId) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    override fun isInGame(userId: Int): Boolean {
        services["game"]?.sessions?.forEach { (_, sessions) ->
            sessions.firstOrNull()?.user?.let {
                if (it.id == userId) {
                    return true
                }
            }
        }
        return false
    }

    override suspend fun sendMessageTo(userId: Int, message: String) {
        services.forEach { (_, service) ->
            service.sessions.forEach { (_, sessions) ->
                if (sessions.firstOrNull()?.user?.id == userId) {
                    sessions.forEach { it.send(message) }
                }
            }
        }
    }

    override suspend fun sendMessageTo(userId: Int, message: SocketApiMessageData) {
        sendMessageTo(userId, SocketApi.encode(message))
    }
}

object SocketServiceFactory {

    fun getService(channel: String, manager: SocketServicesManager): SocketService {
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