package view

import core.component.ConnectionAwareCoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import core.component.DialogBuilder
import core.utils.clearAndFill
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

class LobbyState : CoreRState() {
    var onlineUsers: MutableList<PublicUser>? = null
    var friends: MutableList<PublicUser>? = null
    var loading = false
    var inGame = false
}

/**
 * Lobby component
 */
class Lobby : ConnectionAwareCoreComponent<LobbyProps, LobbyState>() {

    private val friendsService by inject<FriendsService>()
    private val gameService by inject<GameService>()

    override fun LobbyState.init() {
        onlineUsers = mutableListOf()
        friends = mutableListOf()
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
        }
    }

    override fun onBeforeReconnect() {
        setState {
            loading = true
        }
    }

    override fun onBeforeShowConnectionErrorDialog() {
        setState {
            loading = false
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
                showDialog(DialogBuilder()
                    .title("Info")
                    .message("Friendship request send to $email.")
                    .positiveBtn("Ok", null)
                )
                refresh()
            } else {
                window.alert("Friendship request failed. Try it later.")
            }
        }
    }

    private fun removeFriend(id: Int, email: String) {
        showDialog(DialogBuilder()
            .title("Remove friend?")
            .message("Really want to remove $email from friends?")
            .positiveBtn("Yes") {
                componentScope.launch {
                    val res = friendsService.removeFriend(CancelFriendshipRequest(id), user!!.token)
                    if (res.isSuccessful) {
                        refresh()
                    } else {
                        window.alert("Friendship cancel request failed. Try it later.")
                    }
                }
            }
            .negativeBtn("No", null))
    }

    private fun gameRequest(id: Int, email: String) {
        componentScope.launch {
            val res = gameService.createInvitation(CreateGameRequest(id), user!!.token)
            if (res.isSuccessful) {
                showDialog(DialogBuilder()
                    .title("Info")
                    .message("Game invitation send to $email.")
                    .positiveBtn("Ok", null))
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
        super.onReceiveFriendRequest(message)
        if (message.data?.confirm == true) {
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