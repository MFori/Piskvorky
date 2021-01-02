package view

import core.component.CoreComponent
import core.component.CoreRProps
import kotlinx.browser.document
import react.RBuilder
import react.RState
import react.dom.div

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class ProfileState : RState {
    var passwordCurrent: String = ""
    var password: String = ""
    var passwordConfirm: String = ""
    var error: String? = null
}

class Profile : CoreComponent<CoreRProps, RState>() {

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