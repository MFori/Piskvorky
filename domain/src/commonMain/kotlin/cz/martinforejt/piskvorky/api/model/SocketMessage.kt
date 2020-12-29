package cz.martinforejt.piskvorky.api.model

import cz.martinforejt.piskvorky.domain.model.PublicUser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/////////////// CORE

interface SocketApiMessageData

@Serializable
data class SocketApiMessage<T : SocketApiMessageData>(
    @SerialName("action") val action: SocketApiAction,
    @SerialName("data") val data: T?,
    @SerialName("error") val error: Error?
)

@Serializable
enum class SocketApiAction {
    ERROR,
    AUTHORIZE,
    ONLINE_USERS,
    FRIENDS
}

enum class SocketApiCode(val value: Int) {
    OK(200),
    UNAUTHORIZED(401),
    UNKNOWN_ERROR(0)
}

/////////////// Data class for each SocketApiAction

@Serializable
class ErrorSocketApiMessage : SocketApiMessageData

@Serializable
data class AuthorizeSocketApiMessage(
    val token: String
) : SocketApiMessageData

@Serializable
data class OnlineUsersSocketApiMessage(
    val users: List<PublicUser>
) : SocketApiMessageData

@Serializable
data class FriendsSocketApiMessage(
    val users: List<PublicUser>
) : SocketApiMessageData

/////////////// Serializer mappers

@Throws(InvalidSocketMessageException::class)
fun SocketApiMessageData.getAction(): SocketApiAction {
    return when (this) {
        is ErrorSocketApiMessage -> SocketApiAction.ERROR
        is AuthorizeSocketApiMessage -> SocketApiAction.AUTHORIZE
        is OnlineUsersSocketApiMessage -> SocketApiAction.ONLINE_USERS
        is FriendsSocketApiMessage -> SocketApiAction.FRIENDS
        else -> throw InvalidSocketMessageException()
    }
}

@Suppress("UNCHECKED_CAST")
@Throws(InvalidSocketMessageException::class)
fun <T : SocketApiMessageData> SocketApiAction.serializer(): KSerializer<T> {
    return when (this) {
        SocketApiAction.ERROR -> ErrorSocketApiMessage.serializer()
        SocketApiAction.AUTHORIZE -> AuthorizeSocketApiMessage.serializer()
        SocketApiAction.ONLINE_USERS -> OnlineUsersSocketApiMessage.serializer()
        SocketApiAction.FRIENDS -> FriendsSocketApiMessage.serializer()
    } as? KSerializer<T> ?: throw InvalidSocketMessageException()
}