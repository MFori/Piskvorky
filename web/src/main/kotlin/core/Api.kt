package core

import cz.martinforejt.piskvorky.api.model.Error
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.util.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromDynamic
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import kotlin.js.json

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

object Api {

    //const val BASE_URL = "http://http://192.168.0.104/:9090"
    private const val REST_SCHEMA = "http"
    private const val WS_SCHEMA = "ws"
    private const val BASE_URL = "localhost"
    private const val PORT = "9090"
    private const val API_VERSION = "v1"

    @KtorExperimentalAPI
    val client = HttpClient {
        install(WebSockets)
    }

    @ExperimentalSerializationApi
    suspend inline fun <reified T : Any> get(url: String, token: String? = null): ApiResult<T> {
        return getAndParseResult(url.apiUrl(), token)
    }

    @ExperimentalSerializationApi
    suspend inline fun <reified T : Any> post(url: String, body: dynamic, token: String? = null): ApiResult<T> {
        console.log("body $body")
        return postAndParseResult(url.apiUrl(), body, token)
    }

    fun String.apiUrl() = "$REST_SCHEMA://$BASE_URL:$PORT/$API_VERSION$this"

    fun String.wsUrl() = "$WS_SCHEMA://$BASE_URL:$PORT/$API_VERSION$this"

    @KtorExperimentalAPI
    suspend fun webSocket(url: String, block: suspend DefaultClientWebSocketSession.() -> Unit) {
        client.webSocket(url.wsUrl(), {}, block)
    }

    object EP {
        const val LOGIN = "/login"
        const val REGISTER = "/register"
        const val LOST_PASSWORD = "/lost-passwd"
        const val RESET_PASSWORD = "/reset-passwd"
        const val CHANGE_PASSWORD = "/profile/change-passwd"
        const val LOBBY = "/lobby"

        const val FRIENDS_LIST = "/friends"
        const val ADD_FRIEND = "/friends/add"
        const val CANCEL_FRIEND = "/friends/cancel"
        const val FRIEND_REQUESTS = "/friend/requests"
    }
}

data class ApiResult<T>(
    val data: T?,
    val error: Error?,
    val code: Int
) {
    val isSuccess = code == 200
}

fun buildHeader(token: String?, vararg pairs: Pair<String, Any>): kotlin.js.Json {
    val list = pairs.toMutableList()
    token?.let { list.add(Pair("Authorization", "Bearer $token")) }
    return json(*list.toTypedArray())
}

@ExperimentalSerializationApi
inline fun <reified T> parseResponse(json: dynamic): T {
    return Json.decodeFromDynamic(json)
}

@ExperimentalSerializationApi
suspend inline fun <reified T> postAndParseResult(url: String, body: dynamic, token: String?): ApiResult<T> =
    requestAndParseResult("POST", url, body, token)

@ExperimentalSerializationApi
suspend inline fun <reified T> getAndParseResult(url: String, token: String?): ApiResult<T> =
    requestAndParseResult("GET", url, null, token)

@ExperimentalSerializationApi
suspend inline fun <reified T> requestAndParseResult(
    method: String,
    url: String,
    body: dynamic,
    token: String?
): ApiResult<T> {
    val responsePromise = window.fetch(url, object : RequestInit {
        override var method: String? = method
        override var body: dynamic = body
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
        override var headers: dynamic = buildHeader(
            token,
            "Accept" to "application/json",
            "Content-Type" to "application/json",
        )
    })

    val response = try {
        responsePromise.await()
    } catch (e: Throwable) {
        return ApiResult(
            data = null,
            error = Error(0, "Server error"),
            code = 0
        )
    }

    var ok = response.ok
    val json = try {
        response.json().await()
    } catch (e: Throwable) {
        ok = T::class == Unit::class
        JsonObject(emptyMap())
    }

    return ApiResult(
        data = if (ok) parseResponse<T>(json) else null,
        error = if (ok) null else parseResponse<Error>(json),
        code = response.status.toInt()
    )
}
