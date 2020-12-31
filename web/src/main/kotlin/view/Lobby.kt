package view

import core.Api
import core.component.CoreComponent
import core.component.CoreRProps
import core.utils.clearAndFill
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.PublicUser
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.html.id
import react.RBuilder
import react.RState
import react.dom.div
import react.setState

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class LobbyProps : CoreRProps()

class LobbyState : RState {
    var onlineUsers: MutableList<PublicUser>? = null
    var friends: MutableList<PublicUser>? = null
}

class Lobby : CoreComponent<LobbyProps, LobbyState>() {

    private var connection: WebSocketSession? = null

    override fun LobbyState.init() {
        onlineUsers = null
        friends = null
    }

    @KtorExperimentalAPI
    override fun componentDidMount() {
        document.title = "Piskvorky | Lobby"
        state.onlineUsers = mutableListOf()
        state.friends = mutableListOf()
        reconnect()
    }

    override fun componentWillUnmount() {
        componentScope.launch {
            connection?.close(CloseReason(CloseReason.Codes.NORMAL, ""))
        }
    }

    override fun RBuilder.render() {
        div("container-md") {
            attrs.id = "lobby_root"
            coreChild(Header::class)
            div("row") {
                attrs.id = "lobby_content"
                div("col-md") {
                    attrs.id = "lobby_online"
                    coreChild(OnlineUsersPanel::class) {
                        attrs.users = state.onlineUsers
                        attrs.onAction = playerAction
                    }
                }
                div("col-md") {
                    attrs.id = "lobby_friends"
                    coreChild(FriendsPanel::class) {
                        attrs.users = state.friends
                        attrs.onAction = playerAction
                    }
                }
            }
            coreChild(Footer::class)
        }
    }

    @KtorExperimentalAPI
    suspend fun initWebSocket() {
        try {
            Api.webSocket(Api.EP.LOBBY) {
                connection = this
                println("onOpen")
                try {
                    send(SocketApi.encode(AuthorizeSocketApiMessage(user!!.token)))
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            try {
                                receivedMessage(frame.readText())
                            } catch (socketException: SocketApiException) {
                                disconnectOnError()
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    connectionClosed(closeReason.await())
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    connectionClosed(closeReason.await())
                    println("onError ${closeReason.await()}")
                } finally {
                    connection = null
                }
            }
        } catch (e: WebSocketException) {
            showConnectionErrorDialog()
        }
    }

    @KtorExperimentalAPI
    private fun reconnect() {
        componentScope.launch {
            initWebSocket()
        }
    }

    private fun connectionClosed(closeReason: CloseReason?) {
        if (closeReason?.code != CloseReason.Codes.NORMAL.code) {
            showConnectionErrorDialog()
        }
    }

    private fun showConnectionErrorDialog() {

    }

    private fun disconnectOnError() {
        componentScope.launch {
            connection?.close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, ""))
        }
    }

    val playerAction: (PlayerListItem.Action, PlayerVO) -> Unit = { action, playerVO ->
        window.alert("player action $playerVO")
        when (action) {
            PlayerListItem.Action.ADD_FRIEND -> TODO()
            PlayerListItem.Action.REMOVE_FRIEND -> TODO()
            PlayerListItem.Action.PLAY -> TODO()
        }
    }

    private fun refresh() {
        refreshOnline()
    }

    private fun refreshFriends() {
        componentScope.launch {
            connection?.send(SocketApi.encode(SocketApiAction.FRIENDS))
        }
    }

    private fun refreshOnline() {
        componentScope.launch {
            connection?.send(SocketApi.encode(SocketApiAction.ONLINE_USERS))
        }
    }

    private fun receivedMessage(data: String) {
        println("message $data")
        val message = SocketApi.decode(data)
        when (message.action) {
            SocketApiAction.AUTHORIZE -> authorize(message)
            SocketApiAction.ONLINE_USERS -> onlineUsers(message.data as OnlineUsersSocketApiMessage)
            SocketApiAction.FRIENDS -> friends(message.data as FriendsSocketApiMessage)
            else -> {
                disconnectOnError()
            }
        }
    }

    private fun onSockedAuthorized() {
        refresh()
    }

    private fun authorize(message: SocketApiMessage<*>) {
        if (message.error?.code == SocketApiCode.OK.value) {
            onSockedAuthorized()
        } else {
            logout()
        }
    }

    private fun onlineUsers(message: OnlineUsersSocketApiMessage) {
        refreshFriends()
        setState {
            onlineUsers?.clearAndFill(message.users.filter { it.email != user!!.email })
            friends?.let { onlineUsers?.removeAll(it) }
        }
    }

    private fun friends(message: FriendsSocketApiMessage) {
        setState {
            friends?.clearAndFill(message.users)
            friends?.let { onlineUsers?.removeAll(it) }
        }
    }
}