package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.routing.exception.ApiException
import cz.martinforejt.piskvorky.server.routing.utils.errorResponse
import cz.martinforejt.piskvorky.server.security.JWT_AUTH_ADMIN
import cz.martinforejt.piskvorky.server.security.JWT_AUTH_USER
import cz.martinforejt.piskvorky.server.security.SESSION_AUTH_ADMIN
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.util.*

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

const val API_VERSION = "/v1"

@KtorExperimentalAPI
fun Application.registerRoutes() {
    routing {
        authenticate(JWT_AUTH_USER) {
            route(API_VERSION) {
                profileRoutes()
                friendsRoutes()
                gameCreationRoutes()
                gameRoutes()
            }
        }

        authenticate(SESSION_AUTH_ADMIN, JWT_AUTH_ADMIN) {
            route(API_VERSION) {
                adminApiRoutes()
            }
            adminWebRoutes()
        }
        adminPublicWebRoutes()

        route(API_VERSION) {
            socketRoute()
        }
    }

    install(StatusPages) {
        exception<ApiException> { cause ->
            call.errorResponse(cause)
        }
        exception<Throwable> { cause ->
            application.environment.log.error(cause)
            call.errorResponse(cause, HttpStatusCode.InternalServerError)
        }
    }
    intercept(ApplicationCallPipeline.Fallback) {
        throw ApiException(HttpStatusCode.NotFound)
    }
}