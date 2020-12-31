package cz.martinforejt.piskvorky.server.features.lobby

import cz.martinforejt.piskvorky.server.core.service.SocketBroadcaster
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.core.service.SocketServiceSession
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
abstract class LobbyService(
    socketServicesManager: SocketServicesManager
) : SocketService("lobby", socketServicesManager)

abstract class LobbySession(
    sessionId: String,
    channel: String,
    broadcaster: SocketBroadcaster
) : SocketServiceSession(sessionId, channel, broadcaster)