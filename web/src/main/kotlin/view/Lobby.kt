package view

import core.ApiClient
import core.component.CoreComponent
import core.component.CoreRProps
import core.utils.clearAndFill
import core.utils.connectionErrorDialog
import cz.martinforejt.piskvorky.api.Api
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.service.FriendsService
import cz.martinforejt.piskvorky.domain.service.GameService
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.html.id
import org.koin.core.inject
import react.RBuilder
import react.RState
import react.dom.div
import react.router.dom.redirect
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
    var loading = false
    var showErrorDialog = false
    var inGame = false
}

class Lobby : CoreComponent<LobbyProps, LobbyState>() {

    private var connection: WebSocketSession? = null
    private val friendsService by inject<FriendsService>()
    private val gameService by inject<GameService>()

    override fun LobbyState.init() {
        onlineUsers = mutableListOf()
        friends = mutableListOf()
        showErrorDialog = false
        loading = true
        inGame = false
    }

    @KtorExperimentalAPI
    override fun componentDidMount() {
        document.title = "Piskvorky | Lobby"
        reconnect()
    }

    override fun componentWillUnmount() {
        componentScope.launch {
            connection?.close(CloseReason(CloseReason.Codes.NORMAL, ""))
        }
    }

    @KtorExperimentalAPI
    override fun RBuilder.render() {
        if(state.inGame) {
            redirect("/lobby", "/game")
        }
        div("container-md") {
            attrs.id = "lobby_root"
            coreChild(Header::class)
            div("row") {
                attrs.id = "lobby_content"
                div("col-md") {
                    attrs.id = "lobby_online"
                    coreChild(OnlineUsersPanel::class) {
                        attrs.users = if (state.loading) null else state.onlineUsers
                        attrs.onAction = playerAction
                    }
                }
                div("col-md") {
                    attrs.id = "lobby_friends"
                    coreChild(FriendsPanel::class) {
                        attrs.users = if (state.loading) null else state.friends
                        attrs.onAction = playerAction
                    }
                }
            }
            coreChild(Footer::class)
            if (state.showErrorDialog) {
                connectionErrorDialog {
                    setState {
                        showErrorDialog = false
                        loading = true
                    }
                    reconnect()
                }
            }
        }
    }

    @KtorExperimentalAPI
    suspend fun initWebSocket() {
        try {
            ApiClient.webSocket(Api.EP.LOBBY) {
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
        setState {
            loading = false
            showErrorDialog = true
            if (onlineUsers == null) onlineUsers = mutableListOf()
            if (friends == null) friends = mutableListOf()
        }
    }

    private fun disconnectOnError() {
        componentScope.launch {
            connection?.close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, ""))
        }
    }

    val playerAction: (PlayerListItem.Action, PlayerVO) -> Unit = { action, playerVO ->
        when (action) {
            PlayerListItem.Action.ADD_FRIEND -> addFriend(playerVO.id, playerVO.email)
            PlayerListItem.Action.REMOVE_FRIEND -> removeFriend(playerVO.id, playerVO.email)
            PlayerListItem.Action.PLAY -> gameRequest(playerVO.id, playerVO.email)
        }
    }

    private fun refresh() {
        state.onlineUsers = mutableListOf()
        state.friends = mutableListOf()
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
            SocketApiAction.FRIENDSHIP_REQUEST -> friendRequest(message.data as FriendShipRequestSocketApiMessage)
            SocketApiAction.FRIENDSHIP_CANCELLED -> refresh()
            SocketApiAction.GAME_UPDATE -> gameUpdate(message.data as GameUpdateSocketApiMessage)
            SocketApiAction.GAME_REQUEST -> receivedGameRequest(message.data as GameRequestSocketApiMessage)
            else -> {
                if(message.error?.code == SocketApiCode.ALREADY_CONNECTED.value) {
                    window.alert("Already connected on other device.")
                    logout()
                }
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
            loading = false
            onlineUsers?.clearAndFill(message.users.filter { it.email != user!!.email })
            friends?.let { onlineUsers?.removeAll(it) }
        }
    }

    private fun friends(message: FriendsSocketApiMessage) {
        setState {
            loading = false
            friends?.clearAndFill(message.users)
            friends?.let { onlineUsers?.removeAll(it) }
        }
    }

    private fun friendRequest(message: FriendShipRequestSocketApiMessage) {
        if (message.request) {
            if (window.confirm("Friendship request from ${message.email}. Add friend?")) {
                componentScope.launch {
                    friendsService.addFriend(CreateFriendshipRequest(message.userId), user!!.token)
                }
            } else {
                componentScope.launch {
                    friendsService.removeFriend(CancelFriendshipRequest(message.userId), user!!.token)
                }
            }
        } else if (message.confirm) {
            refresh()
        }
    }

    private fun receivedGameRequest(message: GameRequestSocketApiMessage) {
        if(window.confirm("Game request from ${message.email}. Are you ready to play?")) {
            componentScope.launch {
                gameService.createInvitation(CreateGameRequest(message.userId), user!!.token)
            }
        }
    }

    private fun gameUpdate(message: GameUpdateSocketApiMessage) {
        if(message.game.status == GameSnap.Status.running) {
            setState {
                inGame = true
            }
        }
    }

    private fun addFriend(id: Int, email: String) {
        componentScope.launch {
            val res = friendsService.addFriend(CreateFriendshipRequest(id), user!!.token)
            if (res.isSuccessful) {
                window.alert("Friendship request send to $email")
                refresh()
            } else {
                window.alert("Friendship request failed. Try it later.")
            }
        }
    }

    private fun removeFriend(id: Int, email: String) {
        componentScope.launch {
            val res = friendsService.removeFriend(CancelFriendshipRequest(id), user!!.token)
            if (res.isSuccessful) {
                window.alert("Friendship with $email cancelled.")
                refresh()
            } else {
                window.alert("Friendship cancel request failed. Try it later.")
            }
        }
    }

    private fun gameRequest(id: Int, email: String) {
        componentScope.launch {
            val res = gameService.createInvitation(CreateGameRequest(id), user!!.token)
            if (res.isSuccessful) {
                window.alert("Game invitation send to $email.")
                refresh()
            } else {
                window.alert("Game invitation request failed. Try it later.")
            }
        }
    }
}