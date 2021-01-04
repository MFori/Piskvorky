package core.component

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.service.GameService
import io.ktor.http.cio.websocket.*
import kotlinx.browser.window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.asGameVO
import org.koin.core.inject
import react.RBuilder
import react.RState
import react.setState
import service.ConnectionListener
import service.MessageListener
import service.WebSocketService

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
abstract class ConnectionAwareCoreComponent<P : CoreRProps, S : RState> : CoreComponent<P, S>(), MessageListener,
    ConnectionListener {

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

    fun reconnect() {
        props.context?.socketService?.setConnectionListener(this)
        props.context?.socketService?.setMessageListener(this)
        componentScope.launch {
            props.context?.socketService?.open()
        }
    }

    abstract fun showConnectionErrorDialog()

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
    }

    override fun onReceiveFriendCancel(message: SocketApiMessage<FriendshipCancelledSocketApiMessage>) {
    }

    override fun onReceiveGameUpdate(message: SocketApiMessage<GameUpdateSocketApiMessage>) {
    }

    override fun onReceiveGameRequest(message: SocketApiMessage<GameRequestSocketApiMessage>) {
        if (window.confirm("Game request from ${message.data!!.email}. Are you ready to play?")) {
            componentScope.launch {
                gameService.createInvitation(CreateGameRequest(message.data!!.userId), user!!.token)
            }
        }
    }

    override fun onReceiveError(message: SocketApiMessage<*>) {
        if (message.error?.code == SocketApiCode.ALREADY_CONNECTED.value) {
            window.alert("Already connected on other device.")
            logout()
        }
    }
}