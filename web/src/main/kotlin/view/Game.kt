package view

import core.component.ConnectionAwareCoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import core.component.DialogBuilder
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.service.GameService
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import model.GameVO
import model.asGameVO
import org.koin.core.inject
import org.w3c.dom.events.Event
import react.*
import react.dom.button
import react.dom.div
import react.router.dom.redirect

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class GameProps : CoreRProps()

class GameState : CoreRState() {
    var game: GameVO? = null
    var inGame = true
    var unread = 0
    var zoom = 0
    var center = false
}

class Game : ConnectionAwareCoreComponent<GameProps, GameState>() {

    private val gameService by inject<GameService>()
    private var board: GameBoard? = null

    override fun GameState.init() {
        game = null
        inGame = true
        unread = 0
        zoom = 0
        center = false
    }

    override fun componentDidMount() {
        super.componentDidMount()
        document.title = "Piskvorky | Game"
        window.onbeforeunload = { "" }
        window.onpopstate = { window.history.go(1) }
        window.onresize = {
            setState { }
        }
    }

    override fun componentWillUnmount() {
        super.componentWillUnmount()
        window.onbeforeunload = null
        window.onpopstate = null
        window.onresize = null
    }

    override fun RBuilder.render() {
        if (!state.inGame) {
            redirect("/game", "/lobby")
            return
        }
        div("game-container") {
            div("container-md") {
                gameHeader(this@Game, state.game)
                div("game-content") {
                    if (state.game == null) {
                        loading()
                    } else {
                        div("zoom-container") {
                            button(type = ButtonType.button, classes = "zoom-center") {
                                attrs.title = "Center"
                                attrs.onClickFunction = {
                                    setState { center = true }
                                }
                                +"."
                            }
                            button(type = ButtonType.button, classes = "zoom-in") {
                                attrs.title = "Zoom in"
                                attrs.onClickFunction = {
                                    if (state.zoom < 6) setState { zoom++ }
                                }
                                +"+"
                            }
                            button(type = ButtonType.button, classes = "zoom-out") {
                                attrs.title = "Zoom out"
                                attrs.onClickFunction = {
                                    if (state.zoom > -6) setState { zoom-- }
                                }
                                +"-"
                            }
                        }
                    }
                }
                gameFooter(this@Game, state.unread, onGiveUpClicked, onShowChat)
                coreChild(GameFooter::class)
            }
            gameBoard(this@Game, state.game, state.zoom, state.center, onMove)
        }
    }

    override fun componentDidUpdate(prevProps: GameProps, prevState: GameState, snapshot: Any) {
        state.center = false
    }

    override fun refresh() {
        super.refresh()
    }

    private val onGiveUpClicked: (Event) -> Unit = {
        showDialog(DialogBuilder()
            .title("Really give up game?")
            .message("Really give up game?")
            .positiveBtn("Yes") {
                componentScope.launch {
                    val res = gameService.giveUp(user!!.token)
                    if (res.isSuccessful) {
                        setState { inGame = false }
                    }
                }
            }
            .negativeBtn("No", null))
    }

    private val onShowChat: (Event) -> Unit = {

    }

    private val onMove: (Int, Int) -> Unit = { x, y ->
        componentScope.launch {
            val res = gameService.play(Move(x, y), user!!.token)
            if (!res.isSuccessful) {
                showDialog(
                    DialogBuilder()
                        .title("Invalid move")
                        .message("Invalid move, try it better again.")
                        .positiveBtn("Ok", null)
                )
                refresh()
            }
        }
    }

    override fun onReceiveGameUpdate(message: SocketApiMessage<GameUpdateSocketApiMessage>) {
        super.onReceiveGameUpdate(message)
        when (message.data?.game?.status) {
            GameSnap.Status.running -> {
                setState {
                    inGame = true
                    game = message.data!!.game.asGameVO()
                }
            }
            GameSnap.Status.end -> {
                val finishedGame = message.data!!.game.asGameVO()
                setState {
                    game = finishedGame
                }
                showGameFinishDialog(finishedGame)
            }
            else -> {
                setState {
                    inGame = false
                }
            }
        }
    }

    override fun onReceiveGameRequest(message: SocketApiMessage<GameRequestSocketApiMessage>) {
        if (!state.inGame) {
            super.onReceiveGameRequest(message)
        }
    }

    private fun showGameFinishDialog(game: GameVO) {
        showDialog(DialogBuilder()
            .title("Game end.")
            .message(if (game.winner == game.player(user!!.email)) "You are winner!" else "You are looser!")
            .positiveBtn("Ok") {
                componentScope.launch { gameService.giveUp(user!!.token) }
                setState {
                    inGame = false
                }
            })
    }
}