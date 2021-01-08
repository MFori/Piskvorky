package service

import core.ApiClient
import cz.martinforejt.piskvorky.api.Api
import cz.martinforejt.piskvorky.api.model.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface WebSocketService {

    suspend fun open()

    suspend fun send(data: String)

    suspend fun close(reason: CloseReason)

    fun setMessageListener(listener: MessageListener)

    fun setConnectionListener(listener: ConnectionListener)

    fun removeMessageListener(listener: MessageListener)

    fun removeConnectionListener(listener: ConnectionListener)

    fun authorized() : Boolean

    fun setAuthorized()
}

class WebSocketServiceImpl : WebSocketService {
    private var connection: WebSocketSession? = null
    private var messageListener: MessageListener? = null
    private var connectionListener: ConnectionListener? = null

    private var openRequests = 0
    private var authorized = false

    @KtorExperimentalAPI
    override suspend fun open() {
        openRequests++
        if (connection != null && connection!!.isActive) {
            connectionListener?.onConnect(this)
            return
        }

        try {
            ApiClient.webSocket(Api.EP.LOBBY) {
                connection = this
                println("onOpen")
                connectionListener?.onConnect(this@WebSocketServiceImpl)
                try {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            try {
                                messageListener?.onReceiveMessage(frame.readText())
                            } catch (socketException: SocketApiException) {
                                close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Invalid format"))
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("onClose ${closeReason.await()}")
                } catch (e: Throwable) {
                    println("onError ${closeReason.await()}")
                } finally {
                    connection = null
                    authorized = false
                    connectionListener?.onConnectionClosed(
                        closeReason.await() ?: CloseReason(CloseReason.Codes.INTERNAL_ERROR, "")
                    )
                }
            }
        } catch (e: WebSocketException) {
            connection = null
            authorized = false
            connectionListener?.onConnectionClosed(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Cannot connect"))
        }
    }

    override suspend fun send(data: String) {
        connection?.send(data)
    }

    override suspend fun close(reason: CloseReason) {
        openRequests = 0
        if (reason.code == CloseReason.Codes.NORMAL.code) {
            GlobalScope.launch {
                delay(1000) // wait with open state to other component
                if (openRequests == 0) {
                    connection?.close(reason)
                }
            }
        } else {
            connection?.close(reason)
        }
    }

    override fun setMessageListener(listener: MessageListener) {
        this.messageListener = listener
    }

    override fun removeMessageListener(listener: MessageListener) {
        if(messageListener == listener) {
            messageListener = null
        }
    }

    override fun setConnectionListener(listener: ConnectionListener) {
        this.connectionListener = listener
    }

    override fun removeConnectionListener(listener: ConnectionListener) {
        if(connectionListener == listener) {
            connectionListener = null
        }
    }

    override fun authorized(): Boolean {
        return authorized
    }

    override fun setAuthorized() {
        authorized = true
    }
}

interface ConnectionListener {

    fun onConnect(connection: WebSocketService)

    fun onConnectionClosed(closeReason: CloseReason)

}

interface MessageListener {

    @Suppress("UNCHECKED_CAST")
    fun onReceiveMessage(data: String) {
        println("message $data")
        val message = SocketApi.decode(data)
        when (message.action) {
            SocketApiAction.AUTHORIZE -> onReceiveAuthorize(message as SocketApiMessage<AuthorizeSocketApiMessage>)
            SocketApiAction.ONLINE_USERS -> onReceiveOnlineUsers(message as SocketApiMessage<OnlineUsersSocketApiMessage>)
            SocketApiAction.FRIENDS -> onReceiveFriends(message as SocketApiMessage<FriendsSocketApiMessage>)
            SocketApiAction.FRIENDSHIP_REQUEST -> onReceiveFriendRequest(message as SocketApiMessage<FriendShipRequestSocketApiMessage>)
            SocketApiAction.FRIENDSHIP_CANCELLED -> onReceiveFriendCancel(message as SocketApiMessage<FriendshipCancelledSocketApiMessage>)
            SocketApiAction.GAME_UPDATE -> onReceiveGameUpdate(message as SocketApiMessage<GameUpdateSocketApiMessage>)
            SocketApiAction.GAME_REQUEST_CANCELLED -> onGameRequestCancel(message as SocketApiMessage<GameRequestCancelSocketApiMessage>)
            SocketApiAction.GAME_REQUEST -> onReceiveGameRequest(message as SocketApiMessage<GameRequestSocketApiMessage>)
            else -> {
                onReceiveError(message)
                throw InvalidSocketMessageException()
            }
        }
    }

    fun onReceiveAuthorize(message: SocketApiMessage<AuthorizeSocketApiMessage>)

    fun onReceiveOnlineUsers(message: SocketApiMessage<OnlineUsersSocketApiMessage>)

    fun onReceiveFriends(message: SocketApiMessage<FriendsSocketApiMessage>)

    fun onReceiveFriendRequest(message: SocketApiMessage<FriendShipRequestSocketApiMessage>)

    fun onReceiveFriendCancel(message: SocketApiMessage<FriendshipCancelledSocketApiMessage>)

    fun onReceiveGameUpdate(message: SocketApiMessage<GameUpdateSocketApiMessage>)

    fun onReceiveGameRequest(message: SocketApiMessage<GameRequestSocketApiMessage>)

    fun onGameRequestCancel(message: SocketApiMessage<GameRequestCancelSocketApiMessage>)

    fun onReceiveError(message: SocketApiMessage<*>)

}