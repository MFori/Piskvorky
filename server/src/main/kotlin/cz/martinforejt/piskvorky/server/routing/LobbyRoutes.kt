package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.SocketApiException
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager
import cz.martinforejt.piskvorky.server.features.lobby.LobbyService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import java.time.Duration

/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

@KtorExperimentalAPI
fun Route.lobbyRoutes() {

    val socketServicesManager by inject<SocketServicesManager>()

    install(Sessions) {
        cookie<LobbyCookieSession>("LOBBY_SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<LobbyCookieSession>() == null) {
            call.sessions.set(LobbyCookieSession(generateNonce()))
        }
    }

    webSocket("/lobby") {
        this.pingInterval = Duration.ofSeconds(5)
        val cookieSession = call.sessions.get<LobbyCookieSession>()
        if (cookieSession == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        println("onOpen ${cookieSession.id}")

        val lobbyService = socketServicesManager.getService("lobby")
        val session = lobbyService.newConnection(cookieSession.id, this)

        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    try {
                        session.receivedMessage(frame.readText())
                    } catch (socketException: SocketApiException) {
                        socketException.buildMessage()?.let { session.send(it) }
                    }
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("onClose ${closeReason.await()}")
        } catch (e: Throwable) {
            println("onError ${closeReason.await()}")
        } finally {
            println("lobby end")
            lobbyService.disconnect(session)
        }
    }

}

data class LobbyCookieSession(val id: String)