package view

import core.component.ConnectionAwareCoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import cz.martinforejt.piskvorky.api.model.GameSnap
import cz.martinforejt.piskvorky.api.model.GameUpdateSocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApiMessage
import kotlinx.browser.document
import react.RBuilder
import react.dom.div
import react.router.dom.redirect
import react.setState

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class ProfileState : CoreRState() {
    var inGame = false
}

/**
 * Profile component
 */
class Profile : ConnectionAwareCoreComponent<CoreRProps, ProfileState>() {

    override fun ProfileState.init() {
        inGame = false
    }

    override fun componentDidMount() {
        super.componentDidMount()
        document.title = "Piskvorky | Profile"
    }

    override fun RBuilder.render() {
        if (state.inGame) {
            redirect("/lobby", "/game")
            return
        }
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

    override fun onReceiveGameUpdate(message: SocketApiMessage<GameUpdateSocketApiMessage>) {
        if (message.data?.game?.status == GameSnap.Status.running) {
            setState {
                inGame = true
            }
        }
    }
}