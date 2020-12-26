package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.routing.exception.ApiException
import cz.martinforejt.piskvorky.server.routing.utils.errorResponse
import cz.martinforejt.piskvorky.server.security.BASIC_AUTH_ADMIN
import cz.martinforejt.piskvorky.server.security.JWT_AUTH_ADMIN
import cz.martinforejt.piskvorky.server.security.JWT_AUTH_USER
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
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
        authenticate(JWT_AUTH_USER) {
            route(API_VERSION) {
                profileRoutes()
                exampleRoutes()
            }
        }

        authenticate(BASIC_AUTH_ADMIN, JWT_AUTH_ADMIN) {
            route(API_VERSION) {
                adminApiRoutes()
            }
            adminWebRoutes()
        }

        route(API_VERSION) {
            gameRoutes()
        }
    }

    install(StatusPages) {
        exception<ApiException> { cause ->
            call.errorResponse(cause)
        }
        exception<Throwable> { cause ->
            call.errorResponse(cause, HttpStatusCode.InternalServerError)
        }
    }
    intercept(ApplicationCallPipeline.Fallback) {
        throw ApiException(HttpStatusCode.NotFound)
    }
}