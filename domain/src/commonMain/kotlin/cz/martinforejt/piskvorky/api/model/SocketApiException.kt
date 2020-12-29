package cz.martinforejt.piskvorky.api.model

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
open class SocketApiException(message: String? = null) : Exception(message)
class InvalidSocketMessageException : SocketApiException()