package view

import core.component.ConnectionAwareCoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import core.component.DialogBuilder
import cz.martinforejt.piskvorky.api.model.GameSnap
import cz.martinforejt.piskvorky.api.model.GameUpdateSocketApiMessage
import cz.martinforejt.piskvorky.api.model.Move
import cz.martinforejt.piskvorky.api.model.SocketApiMessage
import cz.martinforejt.piskvorky.domain.service.GameService
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import model.GameVO
import model.asGameVO
import org.koin.core.inject
import org.w3c.dom.events.Event
import react.*
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
}

class Game : ConnectionAwareCoreComponent<GameProps, GameState>() {

    private val gameService by inject<GameService>()

    override fun GameState.init() {
        game = null
        inGame = true
        unread = 0
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
                    }
                }
                gameFooter(this@Game, state.unread, onGiveUpClicked, onShowChat)
                coreChild(GameFooter::class)
            }
            gameBoard(this@Game, state.game, onMove)
        }
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
                window.alert(res.error?.message ?: "Invalid move.")
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