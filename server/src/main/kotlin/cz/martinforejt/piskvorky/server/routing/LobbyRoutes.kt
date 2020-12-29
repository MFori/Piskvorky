package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.SocketApi
import cz.martinforejt.piskvorky.api.model.SocketApiException
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.features.lobby.LobbySession
import cz.martinforejt.piskvorky.server.security.JwtManager
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

    val jwtManager by inject<JwtManager>()
    val usersRepository by inject<UsersRepository>()

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

        val session = LobbySession(cookieSession.id, this, jwtManager, usersRepository)

        println("onOpen ${cookieSession.id}")
        try {
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    try {
                        session.receivedMessage(frame.readText())
                    } catch (socketException: SocketApiException) {
                        socketException.buildMessage()?.let { outgoing.send(Frame.Text(SocketApi.encode(it))) }
                    }
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("onClose ${closeReason.await()}")
        } catch (e: Throwable) {
            println("onError ${closeReason.await()}")
        } finally {
            println("lobby end")
            session.left()
        }
    }

}

data class LobbyCookieSession(val id: String)