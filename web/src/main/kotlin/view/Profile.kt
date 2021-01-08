package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import kotlinx.browser.document
import react.RBuilder
import react.dom.div

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class Profile : CoreComponent<CoreRProps, CoreRState>() {

    override fun componentDidMount() {
        super.componentDidMount()
        document.title = "Piskvorky | Profile"
    }

    override fun RBuilder.render() {
        div("container-md") {
            coreChild(Header::class)
            div("stretch-content") {
                div("panel-box") {
                    div("panel-title") { +"Hello, ${user!!.email}" }
                }
                div("panel-box mt-2") {
                    div("panel-title") { +"Change password" }
                    coreChild(ChangePassword::class)
                }
            }
            coreChild(Footer::class)
        }
    }

}