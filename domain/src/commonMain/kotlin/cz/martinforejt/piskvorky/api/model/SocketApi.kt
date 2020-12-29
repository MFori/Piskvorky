package cz.martinforejt.piskvorky.api.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object SocketApi {

    fun encode(message: SocketApiMessage<SocketApiMessageData>): String {
        return Json.encodeToString(
            SocketApiMessage.serializer(message.action.serializer()),
            message
        )
    }

    @Throws(InvalidSocketMessageException::class)
    fun encode(action: SocketApiAction): String {
        return encode(action, null)
    }

    @Throws(InvalidSocketMessageException::class)
    fun encode(action: SocketApiAction, error: Error?): String {
        return Json.encodeToString(
            SocketApiMessage.serializer(action.serializer()),
            SocketApiMessage(action, null, error)
        )
    }

    @Throws(InvalidSocketMessageException::class)
    fun encode(message: SocketApiMessageData): String {
        val action = message.getAction()
        return Json.encodeToString(
            SocketApiMessage.serializer(action.serializer()),
            SocketApiMessage(action, message, null)
        )
    }

    @Throws(InvalidSocketMessageException::class)
    fun decode(data: String): SocketApiMessage<SocketApiMessageData> {
        try {
            val json = Json.parseToJsonElement(data)
            val action = SocketApiAction.valueOf(
                json.jsonObject["action"]?.jsonPrimitive?.content ?: throw InvalidSocketMessageException()
            )
            return Json.decodeFromJsonElement(SocketApiMessage.serializer(action.serializer()), json)
        } catch (e: Throwable) {
            throw InvalidSocketMessageException()
        }
    }
}