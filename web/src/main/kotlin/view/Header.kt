package view

import core.component.CoreComponent
import core.component.CoreRProps
import kotlinx.html.id
import react.RBuilder
import react.RState
import react.dom.div
import react.dom.img
import react.dom.span
import react.router.dom.routeLink

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class Header : CoreComponent<CoreRProps, RState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            attrs {
                id = "header"
            }
            img(classes = "mg-4", src = "/images/logo2.png", alt = "logo") {}

            div {
                attrs.id = "user-box"
                span { +(user?.email ?: "") }
                +" | "
                routeLink("/logout") {+"logout"}
            }
        }
    }

}