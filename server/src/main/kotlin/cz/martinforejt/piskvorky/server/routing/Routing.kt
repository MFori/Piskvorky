package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.routing.exception.ApiException
import cz.martinforejt.piskvorky.server.security.JWT_AUTH_NAME
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

const val API_VERSION = "/v1"

fun Application.registerRoutes() {
    routing {
        authenticate(JWT_AUTH_NAME) {
            route(API_VERSION) {
                profileRoutes()
                exampleRoutes()
            }

        }
    }

    install(StatusPages) {
        exception<ApiException> { cause ->
            call.respond(cause.httpStatusCode, mapOf("message" to (cause.message ?: cause.localizedMessage)))
        }
        exception<Throwable> { cause ->
            call.respond(
                message = mapOf("message" to cause.localizedMessage),
                status = HttpStatusCode.InternalServerError
            )
        }
    }
    intercept(ApplicationCallPipeline.Fallback) {
        throw ApiException(HttpStatusCode.NotFound)
    }
}