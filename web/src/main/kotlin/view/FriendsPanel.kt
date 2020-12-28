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

class FriendsPanel : CoreComponent<CoreRProps, RState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            div("font-weight-bold") { +"Friends" }
            for (i in 0..5) {
                div {
                    +"User $i"
                }
            }
        }
    }

}