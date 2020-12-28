package view

import core.component.CoreComponent
import core.component.CoreRProps
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
        div {
            routeLink("/login") {
                +"Login"
            }
            routeLink("/logout") {
                +"Logout"
            }
            routeLink("/game") {
                +"Game"
            }
        }
    }
}