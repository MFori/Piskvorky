package view

import core.component.CoreComponent
import core.component.CoreRProps
import kotlinx.html.id
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