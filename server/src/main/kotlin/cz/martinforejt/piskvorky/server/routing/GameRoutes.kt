package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.security.JwtManager
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Route.gameRoutes() {

    val jwtManager by inject<JwtManager>()

    webSocket("/game") {
        var userId: Int? = null
        println("onConnect")
        try {
            for (frame in incoming){
                val text = (frame as Frame.Text).readText()
                if(userId == null) {
                    val principal = jwtManager.validateToken(text)
                    userId = principal?.id
                }

                outgoing.send(Frame.Text("YOU ($userId) SAID $text"))
                if (text.equals("bye", ignoreCase = true)) {
                    close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("onClose ${closeReason.await()}")
        } catch (e: Throwable) {
            println("onError ${closeReason.await()}")
            //e.printStackTrace()
        }
    }

}