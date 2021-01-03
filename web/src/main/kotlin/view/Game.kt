package view

import core.component.CoreComponent
import core.component.CoreRProps
import kotlinx.browser.document
import react.RBuilder
import react.RState
import react.router.dom.routeLink

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class GameProps : CoreRProps()

class GameState : RState {

}

class Game : CoreComponent<GameProps, GameState>() {

    override fun componentDidMount() {
        document.title = "Piskvorky | Game"
    }

    override fun RBuilder.render() {
        routeLink("/logout") {
            +"Logout"
        }
    }

}