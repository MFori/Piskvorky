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
    var onlineUsers = mutableListOf<PublicUser>()
    var friends = mutableListOf<PublicUser>()
}

class Lobby : CoreComponent<LobbyProps, LobbyState>() {

    private var connection: WebSocketSession? = null

    override fun LobbyState.init() {
        onlineUsers = mutableListOf()
        friends = mutableListOf()
    }

    @KtorExperimentalAPI
    override fun componentDidMount() {
        componentScope.launch {
            initWebSocket()
        }
    }

    override fun componentWillUnmount() {

    }

    override fun RBuilder.render() {
        div("container") {
            attrs.id = "lobby_root"
            coreChild(Header::class)
            div("row") {
                attrs.id = "lobby_content"
                div("col-sm") {
                    attrs.id = "lobby_online"
                    coreChild(OnlineUsersPanel::class) {
                        attrs.users = state.onlineUsers
                    }
                }
                div("col-sm") {
                    attrs.id = "lobby_friends"
                    coreChild(FriendsPanel::class) {
                       attrs.users = state.friends
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
                                // TODO show connection error dialog
                                close()
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("onError ${closeReason.await()}")
                } finally {
                    connection = null
                    println("lobby connection end")
                }
            }
        } catch (e: WebSocketException) {
            println("cant connect")
        }
    }

    private fun refresh() {
        refreshFriends()
        refreshOnline()
    }

    private fun refreshFriends() {
        componentScope.launch {
            println("send friends " + (connection != null))
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
            //SocketApiAction.ERROR -> errorMessage()
            else -> {
                //throw InvalidSocketMessageException()
            }
        }
    }

    private fun onSockedAuthorized() {
        refresh()
    }

    private fun authorize(message: SocketApiMessage<*>) {
        if(message.error?.code == SocketApiCode.OK.value) {
            onSockedAuthorized()
        } else {
            logout()
        }
    }

    private fun onlineUsers(message: OnlineUsersSocketApiMessage) {
        setState {
            onlineUsers.clearAndFill(message.users)
        }
    }

    private fun friends(message: FriendsSocketApiMessage) {
        setState {
            friends.clearAndFill(message.users)
        }
    }
}