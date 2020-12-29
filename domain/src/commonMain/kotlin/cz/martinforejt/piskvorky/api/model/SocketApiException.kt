package cz.martinforejt.piskvorky.api.model


/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
open class SocketApiException(private val code: SocketApiCode? = null, message: String? = null) : Exception(message) {

    fun buildMessage(): SocketApiMessage<SocketApiMessageData>? {
        if (code == null) {
            return null
        }
        return SocketApiMessage(
            SocketApiAction.ERROR,
            null,
            Error(code.value, message ?: "Socket api error.")
        )
    }

}

class InvalidSocketMessageException : SocketApiException(SocketApiCode.UNKNOWN_ERROR, "Invalid socket api message format!")

class UnauthorizedSocketException : SocketApiException(SocketApiCode.UNAUTHORIZED, "Unauthorized")