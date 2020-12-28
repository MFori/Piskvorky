package view

import core.component.CoreComponent
import core.component.CoreRProps
import react.RBuilder
import react.RState
import react.dom.div

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class OnlineUsersPanel : CoreComponent<CoreRProps, RState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            div("font-weight-bold") { +"Online users" }
            for (i in 0..5) {
                div {
                    +"User $i"
                }
            }
        }
    }

}