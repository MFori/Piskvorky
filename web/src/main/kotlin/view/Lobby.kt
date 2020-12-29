package view

import core.component.CoreComponent
import core.component.CoreRProps
import cz.martinforejt.piskvorky.api.model.AuthorizeSocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApi
import kotlinx.html.id
import org.w3c.dom.WebSocket
import react.RBuilder
import react.RState
import react.dom.div
import react.router.dom.routeLink

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class LobbyProps : CoreRProps()

class LobbyState : RState {
}

class Lobby : CoreComponent<LobbyProps, LobbyState>() {

    private val client : WebSocket = WebSocket("ws://localhost:9090/v1/lobby")

    override fun componentWillMount() {
        client.onopen = {
            console.log("lobby socket open")
        }
        client.onmessage = {
            console.log("lobby socket message ${it.data}")
            client.send(SocketApi.encode(AuthorizeSocketApiMessage(user!!.token)))
        }
        client.onerror = {
            console.log("lobby socket error")
        }
        client.onclose = {
            console.log("lobby socket close")
        }
    }

    override fun RBuilder.render() {
        div("container") {
            attrs.id = "lobby_root"
            coreChild(Header::class)
            div("row") {
                attrs.id = "lobby_content"
                div("col-sm") {
                    attrs.id = "lobby_online"
                    coreChild(OnlineUsersPanel::class)
                }
                div("col-sm") {
                    attrs.id = "lobby_friends"
                    coreChild(FriendsPanel::class)
                }
            }
            coreChild(Footer::class)
        }
    }
}