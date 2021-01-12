package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import kotlinx.html.id
import react.RBuilder
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

/**
 * Header component
 */
class Header : CoreComponent<CoreRProps, CoreRState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            attrs {
                id = "header"
            }
            routeLink("/lobby") {
                img(classes = "mg-4", src = "/images/logo2.png", alt = "logo") {}
            }

            div {
                attrs.id = "user-box"
                routeLink("/profile") {
                    span { +(user?.email ?: "") }
                }
                +" | "
                routeLink("/logout") {+"logout"}
            }
        }
    }

}