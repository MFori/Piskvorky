package cz.martinforejt.piskvorky.server.features.game

import cz.martinforejt.piskvorky.server.core.service.SocketServiceSession
import cz.martinforejt.piskvorky.server.core.service.SocketServiceSessionFactory
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager
import io.ktor.http.cio.websocket.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Martin Forejt on 31.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class GameServiceImpl(
    socketServicesManager: SocketServicesManager
) : GameService(socketServicesManager) {

    override val sessions: MutableMap<String, MutableList<SocketServiceSession>> =
        ConcurrentHashMap<String, MutableList<SocketServiceSession>>()

    override fun newConnection(sessionId: String, connection: WebSocketSession): SocketServiceSession {
        val session = SocketServiceSessionFactory.getSession(sessionId, channel, this, connection)

        val list = sessions.computeIfAbsent(sessionId) { CopyOnWriteArrayList() }
        list.add(session)

        return session
    }

}