package view

import core.component.ConnectionAwareCoreComponent
import core.component.CoreComponent
import core.component.CoreRProps
import core.utils.connectionErrorDialog
import cz.martinforejt.piskvorky.api.model.AuthorizeSocketApiMessage
import cz.martinforejt.piskvorky.api.model.GameSnap
import cz.martinforejt.piskvorky.api.model.GameUpdateSocketApiMessage
import cz.martinforejt.piskvorky.api.model.SocketApiMessage
import cz.martinforejt.piskvorky.domain.model.Game
import cz.martinforejt.piskvorky.domain.service.GameService
import kotlinx.browser.document
import kotlinx.coroutines.launch
import kotlinx.html.id
import model.GameVO
import model.asGameVO
import org.koin.core.inject
import react.RBuilder
import react.RState
import react.dom.div
import react.router.dom.redirect
import react.router.dom.routeLink
import react.setState
import service.WebSocketService

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class GameProps : CoreRProps()

class GameState : RState {
    var game: GameVO? = null
    var inGame = true
    var showErrorDialog = false
}

class Game : ConnectionAwareCoreComponent<GameProps, GameState>() {

    private val gameService by inject<GameService>()

    override fun GameState.init() {
        game = null
        inGame = true
        showErrorDialog = false
    }

    override fun componentDidMount() {
        super.componentDidMount()
        document.title = "Piskvorky | Game"
    }

    override fun RBuilder.render() {
        if (!state.inGame) {
            redirect("/game", "/lobby")
            return
        }
        div("container-md") {
            coreChild(Header::class)
            div("stretch-content") {
                if (state.game == null) {
                    loading()
                } else {
                    div {
                        +state.game!!.cross.email
                    }
                    div {
                        +state.game!!.nought.email
                    }
                }
            }
            coreChild(Footer::class)
            if (state.showErrorDialog) {
                connectionErrorDialog {
                    setState {
                        showErrorDialog = false
                    }
                    reconnect()
                }
            }
        }
    }

    override fun refresh() {
        super.refresh()
    }

    override fun onReceiveGameUpdate(message: SocketApiMessage<GameUpdateSocketApiMessage>) {
        super.onReceiveGameUpdate(message)
        if (message.data?.game?.status == GameSnap.Status.running) {
            setState {
                inGame = true
                game = message.data!!.game.asGameVO()
            }
        } else {
            setState {
                inGame = false
            }
        }
    }

    override fun showConnectionErrorDialog() {
        TODO("Not yet implemented")
    }
}