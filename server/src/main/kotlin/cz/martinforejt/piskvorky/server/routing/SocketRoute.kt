package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.SocketApiException
import cz.martinforejt.piskvorky.server.core.service.SocketService
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
fun Route.socketRoute() {

    val socketService by inject<SocketService>()

    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<SocketCookieSession>() == null) {
            call.sessions.set(SocketCookieSession(generateNonce()))
        }
    }

    webSocket("/lobby") {
        this.pingInterval = Duration.ofSeconds(5)
        val cookieSession = call.sessions.get<SocketCookieSession>()
        if (cookieSession == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        println("onOpen ${cookieSession.id}")

        val session = socketService.newConnection(cookieSession.id, this)

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
            socketService.disconnect(session)
        }
    }

}

data class SocketCookieSession(val id: String)