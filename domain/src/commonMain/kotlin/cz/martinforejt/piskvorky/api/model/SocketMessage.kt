package cz.martinforejt.piskvorky.api.model

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
    AUTHORIZE
}

/////////////// Data class for each SocketApiAction

@Serializable
data class AuthorizeSocketApiMessage(
    val token: String
) : SocketApiMessageData

/////////////// Serializer mappers

fun SocketApiMessageData.getAction() : SocketApiAction {
    return when (this) {
        is AuthorizeSocketApiMessage -> SocketApiAction.AUTHORIZE
        else -> throw InvalidSocketMessageException()
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : SocketApiMessageData> SocketApiAction.serializer() : KSerializer<T> {
    return when (this) {
        SocketApiAction.AUTHORIZE -> AuthorizeSocketApiMessage.serializer() as? KSerializer<T>
    } ?: throw InvalidSocketMessageException()
}