package view

import core.component.ConnectionAwareCoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import core.component.DialogBuilder
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.ChatMessage
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
import react.dom.img
import react.router.dom.redirect
import kotlin.js.Date

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
    var chatOpen = false
    var helpOpen = false
    var messages: MutableList<ChatMessage>? = null
}

/**
 * Game component
 */
class Game : ConnectionAwareCoreComponent<GameProps, GameState>() {

    private val gameService by inject<GameService>()

    override fun GameState.init() {
        game = null
        inGame = true
        unread = 0
        zoom = 0
        center = false
        chatOpen = false
        messages = mutableListOf()
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
                        div("help-container") {
                            button(type = ButtonType.button, classes = "btn btn-light help") {
                                attrs.title = "Help"
                                attrs.onClickFunction = {
                                    setState { helpOpen = true }
                                }
                                img(src = "/icons/question-circle.svg") {}
                            }
                        }
                        div("zoom-container") {
                            button(type = ButtonType.button, classes = "btn btn-light zoom-center") {
                                attrs.title = "Center"
                                attrs.onClickFunction = {
                                    setState { center = true }
                                }
                                img(src = "/icons/x-circle-fill.svg") {}
                            }
                            button(type = ButtonType.button, classes = "btn btn-light zoom-in") {
                                attrs.title = "Zoom in"
                                attrs.onClickFunction = {
                                    if (state.zoom < 6) setState { zoom++ }
                                }
                                img(src = "/icons/zoom-in.svg") {}
                            }
                            button(type = ButtonType.button, classes = "btn btn-light zoom-out") {
                                attrs.title = "Zoom out"
                                attrs.onClickFunction = {
                                    if (state.zoom > -6) setState { zoom-- }
                                }
                                img(src = "/icons/zoom-out.svg") {}
                            }
                        }
                    }
                }
                gameFooter(this@Game, state.unread, onGiveUpClicked, onShowChat)
                coreChild(GameFooter::class)
            }
            gameBoard(this@Game, state.game, state.zoom, state.center, onMove)
        }
        if (state.chatOpen) {
            child(ChatDialog::class) {
                attrs {
                    messages = state.messages
                    onSend = onSendChatMessage
                    dismissCallback = onDismissChatDialog
                }
            }
        }
        if (state.helpOpen) {
            child(HelpDialog::class) {
                attrs.dismissCallback = onDismissHelpDialog
            }
        }
    }

    override fun componentDidUpdate(prevProps: GameProps, prevState: GameState, snapshot: Any) {
        state.center = false
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
        setState {
            chatOpen = true
            unread = 0
        }
    }

    private val onSendChatMessage: (String) -> Unit = {
        val message = ChatMessage(user!!.email, it, Date().getMilliseconds().toLong())
        setState {
            if (messages == null) messages = mutableListOf()
            messages!!.add(message)
        }
        componentScope.launch {
            sendChatMessage(message)
        }
    }

    private val onDismissChatDialog: () -> Unit = {
        setState { chatOpen = false }
    }

    private val onDismissHelpDialog: () -> Unit = {
        setState { helpOpen = false }
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

    override fun onReceiveChatMessage(message: SocketApiMessage<ChatMessageSocketApiMessage>) {
        setState {
            if (!chatOpen) unread++
            if (messages == null) messages = mutableListOf()
            message.data?.message?.let { messages!!.add(it) }
        }
    }
}