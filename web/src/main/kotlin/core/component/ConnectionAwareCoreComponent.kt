package core.component

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.ChatMessage
import cz.martinforejt.piskvorky.domain.service.FriendsService
import cz.martinforejt.piskvorky.domain.service.GameService
import io.ktor.http.cio.websocket.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.koin.core.inject
import react.RBuilder
import service.ConnectionListener
import service.MessageListener
import service.WebSocketService

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
abstract class ConnectionAwareCoreComponent<P : CoreRProps, S : CoreRState> : CoreComponent<P, S>(), MessageListener,
    ConnectionListener {

    private val friendsService by inject<FriendsService>()
    private val gameService by inject<GameService>()

    override fun componentDidMount() {
        super.componentDidMount()
        reconnect()
    }

    override fun componentWillUnmount() {
        super.componentWillUnmount()
        props.context?.socketService?.removeMessageListener(this)
        props.context?.socketService?.removeConnectionListener(this)
        componentScope.launch {
            props.context?.socketService?.close(CloseReason(CloseReason.Codes.NORMAL, ""))
        }
    }

    override fun RBuilder.render() {

    }

    open fun onBeforeReconnect() {}

    private fun reconnect() {
        onBeforeReconnect()
        props.context?.socketService?.setConnectionListener(this)
        props.context?.socketService?.setMessageListener(this)
        componentScope.launch {
            props.context?.socketService?.open()
        }
    }

    open fun onBeforeShowConnectionErrorDialog() {}

    private fun showConnectionErrorDialog() {
        onBeforeShowConnectionErrorDialog()
        showDialog(
            DialogBuilder()
                .title("Connection error")
                .message("Connection error")
                .positiveBtn("Retry") {
                    reconnect()
                }
                .negativeBtn(null, null)
        )
    }

    open fun refresh() {
        componentScope.launch {
            val res = gameService.getGame(user!!.token)
            onReceiveGameUpdate(
                SocketApiMessage(
                    SocketApiAction.GAME_UPDATE,
                    res.data?.let { GameUpdateSocketApiMessage(it) },
                    res.error?.let { Error(it.code, it.message ?: "") }
                )
            )
        }
    }

    override fun onConnect(connection: WebSocketService) {
        if (props.context?.socketService?.authorized() != true) {
            componentScope.launch {
                connection.send(SocketApi.encode(AuthorizeSocketApiMessage(user!!.token)))
            }
        } else {
            refresh()
        }
    }

    override fun onConnectionClosed(closeReason: CloseReason) {
        if (closeReason.code != CloseReason.Codes.NORMAL.code) {
            showConnectionErrorDialog()
        }
    }

    override fun onReceiveAuthorize(message: SocketApiMessage<AuthorizeSocketApiMessage>) {
        if (message.error?.code != SocketApiCode.OK.value) {
            logout()
            return
        }
        props.context?.socketService?.setAuthorized()
        refresh()
    }

    override fun onReceiveOnlineUsers(message: SocketApiMessage<OnlineUsersSocketApiMessage>) {
    }

    override fun onReceiveFriends(message: SocketApiMessage<FriendsSocketApiMessage>) {
    }

    override fun onReceiveFriendRequest(message: SocketApiMessage<FriendShipRequestSocketApiMessage>) {
        if (message.data?.request == true) {
            showDialog(DialogBuilder()
                .title("Add friend?")
                .message("Friendship request from ${message.data!!.email}.")
                .positiveBtn("Yes") {
                    componentScope.launch {
                        friendsService.addFriend(CreateFriendshipRequest(message.data!!.userId), user!!.token)
                    }
                }
                .negativeBtn("No") {
                    componentScope.launch {
                        friendsService.removeFriend(CancelFriendshipRequest(message.data!!.userId), user!!.token)
                    }
                })
        }
    }

    override fun onReceiveFriendCancel(message: SocketApiMessage<FriendshipCancelledSocketApiMessage>) {
    }

    override fun onReceiveGameUpdate(message: SocketApiMessage<GameUpdateSocketApiMessage>) {
    }

    override fun onReceiveGameRequest(message: SocketApiMessage<GameRequestSocketApiMessage>) {
        showDialog(DialogBuilder()
            .title("Are you ready to play?")
            .message("Game request from ${message.data!!.email}.")
            .positiveBtn("Play") {
                componentScope.launch {
                    gameService.createInvitation(CreateGameRequest(message.data!!.userId), user!!.token)
                }
            }
            .negativeBtn("Dismiss") {
                componentScope.launch {
                    gameService.cancelInvitation(CancelGameRequest(message.data!!.userId), user!!.token)
                }
            })
    }

    override fun onGameRequestCancel(message: SocketApiMessage<GameRequestCancelSocketApiMessage>) {
        showDialog(
            DialogBuilder()
                .title("Game request refused.")
                .message("User ${message.data!!.email} refused your game invitation.")
                .positiveBtn("Ok", null)
        )
    }

    override fun onReceiveError(message: SocketApiMessage<*>) {
        if (message.error?.code == SocketApiCode.ALREADY_CONNECTED.value) {
            window.alert("Already connected on other device.")
            logout()
        }
    }

    override fun onReceiveChatMessage(message: SocketApiMessage<ChatMessageSocketApiMessage>) {}

    suspend fun sendChatMessage(message: ChatMessage) {
        props.context?.socketService?.send(SocketApi.encode(ChatMessageSocketApiMessage(message)))
    }
}