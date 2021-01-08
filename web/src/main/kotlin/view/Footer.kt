package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import kotlinx.html.id
import react.RBuilder
import react.dom.a
import react.dom.div

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class Footer : CoreComponent<CoreRProps, CoreRState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            attrs {
                id = "footer"
            }
            +"@ 2020 "
            a(href = "https://martinforejt.cz", target = "_blank") {
                +"Martin Forejt"
            }
        }
    }

}