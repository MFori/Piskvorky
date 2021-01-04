package view

import core.component.ConnectionAwareCoreComponent
import core.component.CoreRProps
import core.utils.clearAndFill
import core.utils.connectionErrorDialog
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.service.FriendsService
import cz.martinforejt.piskvorky.domain.service.GameService
import io.ktor.util.*
import kotlinx.browser.document
import kotlinx.browser.window
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

class Lobby : ConnectionAwareCoreComponent<LobbyProps, LobbyState>() {

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
        super.componentDidMount()
        document.title = "Piskvorky | Lobby"
    }

    @KtorExperimentalAPI
    override fun RBuilder.render() {
        if (state.inGame) {
            redirect("/lobby", "/game")
            return
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

    override fun showConnectionErrorDialog() {
        setState {
            loading = false
            showErrorDialog = true
            if (onlineUsers == null) onlineUsers = mutableListOf()
            if (friends == null) friends = mutableListOf()
        }
    }

    val playerAction: (PlayerListItem.Action, PlayerVO) -> Unit = { action, playerVO ->
        when (action) {
            PlayerListItem.Action.ADD_FRIEND -> addFriend(playerVO.id, playerVO.email)
            PlayerListItem.Action.REMOVE_FRIEND -> removeFriend(playerVO.id, playerVO.email)
            PlayerListItem.Action.PLAY -> gameRequest(playerVO.id, playerVO.email)
        }
    }

    override fun refresh() {
        super.refresh()
        state.onlineUsers = mutableListOf()
        state.friends = mutableListOf()
        refreshOnline()
    }

    private fun refreshFriends() {
        componentScope.launch {
            props.context?.socketService?.send(SocketApi.encode(SocketApiAction.FRIENDS))
        }
    }

    private fun refreshOnline() {
        componentScope.launch {
            props.context?.socketService?.send(SocketApi.encode(SocketApiAction.ONLINE_USERS))
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

    override fun onReceiveOnlineUsers(message: SocketApiMessage<OnlineUsersSocketApiMessage>) {
        refreshFriends()
        setState {
            loading = false
            onlineUsers?.clearAndFill(message.data?.users?.filter { it.email != user!!.email } ?: emptyList())
            friends?.let { onlineUsers?.removeAll(it) }
        }
    }

    override fun onReceiveFriends(message: SocketApiMessage<FriendsSocketApiMessage>) {
        setState {
            loading = false
            friends?.clearAndFill(message.data?.users ?: emptyList())
            friends?.let { onlineUsers?.removeAll(it) }
        }
    }

    override fun onReceiveFriendRequest(message: SocketApiMessage<FriendShipRequestSocketApiMessage>) {
        if (message.data?.request == true) {
            if (window.confirm("Friendship request from ${message.data!!.email}. Add friend?")) {
                componentScope.launch {
                    friendsService.addFriend(CreateFriendshipRequest(message.data!!.userId), user!!.token)
                }
            } else {
                componentScope.launch {
                    friendsService.removeFriend(CancelFriendshipRequest(message.data!!.userId), user!!.token)
                }
            }
        } else if (message.data?.confirm == true) {
            refresh()
        }
    }

    override fun onReceiveFriendCancel(message: SocketApiMessage<FriendshipCancelledSocketApiMessage>) {
        refresh()
    }

    override fun onReceiveGameUpdate(message: SocketApiMessage<GameUpdateSocketApiMessage>) {
        if (message.data?.game?.status == GameSnap.Status.running) {
            setState {
                inGame = true
            }
        }
    }
}