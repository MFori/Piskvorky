package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.AuthorizeSocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApi
import cz.martinforejt.piskvorky.api.model.SocketApiAction
import cz.martinforejt.piskvorky.server.security.JwtManager
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Route.lobbyRoutes() {

    val jwtManager by inject<JwtManager>()

    webSocket("/lobby") {
        var userId: Int? = null
        println("onConnect")
        try {
            GlobalScope.launch {
                while(this.isActive) {
                    delay(1000)
                    outgoing.send(Frame.Text("From server ${System.currentTimeMillis()}"))
                }
            }
            for (frame in incoming){
                val text = (frame as Frame.Text).readText()
                val message = SocketApi.decode(text)
                when (message.action) {
                    SocketApiAction.AUTHORIZE -> {
                        val token = (message.data as AuthorizeSocketApiMessage).token
                        println("authorize token: $token")
                        val principal = jwtManager.validateToken(token)
                        println("authorized: $principal")
                    }
                }

              //  Json.decodeFromString(SocketMessage.serializer(String.serializer()), text)
             //   val message = Json.parse<SocketMessage<String>>(text)
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