package cz.martinforejt.piskvorky.server.features.game

import cz.martinforejt.piskvorky.server.core.service.SocketBroadcaster
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.core.service.SocketServiceSession
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager

/**
 * Created by Martin Forejt on 31.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
abstract class GameService(
    socketServicesManager: SocketServicesManager
) : SocketService("game", socketServicesManager)

abstract class GameSession(
    sessionId: String,
    channel: String,
    broadcaster: SocketBroadcaster
) : SocketServiceSession(sessionId, channel, broadcaster)