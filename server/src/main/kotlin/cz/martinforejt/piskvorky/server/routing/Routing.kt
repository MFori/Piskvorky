package cz.martinforejt.piskvorky.server.routing

import io.ktor.application.*
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

fun Application.registerRoutes() {
    routing {
        securityRoutes()
        usersRoutes()
        exampleRoutes()
    }

    install(StatusPages) {
        /*exception<AuthenticationException> { cause ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { cause ->
            call.respond(HttpStatusCode.Forbidden)
        }*/
        exception<Throwable> { cause ->
            call.respond(
                message = mapOf("mssage" to "Error not found"),
                status = HttpStatusCode.NotFound
            )
        }
    }
    intercept(ApplicationCallPipeline.Fallback) {
        throw IllegalStateException()
    }
}