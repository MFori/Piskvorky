package core

import cz.martinforejt.piskvorky.api.model.Error
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromDynamic
import kotlinx.serialization.json.Json
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

    const val BASE_URL = "http://localhost:9090"
    const val API_VERSION = "v1"

    @ExperimentalSerializationApi
    suspend inline fun <reified T : Any> get(url: String, token: String? = null): ApiResult<T> {
        return getAndParseResult(url.apiUrl(), token)
    }

    @ExperimentalSerializationApi
    suspend inline fun <reified T : Any> post(url: String, body: dynamic, token: String? = null): ApiResult<T> {
        console.log("body $body")
        return postAndParseResult(url.apiUrl(), body, token)
    }

    fun String.apiUrl() = "$BASE_URL/$API_VERSION$this"
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

    val json = response.json().await()
    return ApiResult(
        data = if (response.ok) parseResponse<T>(json) else null,
        error = if (response.ok) null else parseResponse<Error>(json),
        code = response.status.toInt()
    )
}
