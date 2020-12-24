package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.routing.exception.AuthenticationException
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Route.profileRoutes() {

    route("/profile") {

        get("/") {
            val user = call.principal<UserPrincipal>()

            if(user != null) {
                call.respond(mapOf("email" to user.email))
            } else {
                throw AuthenticationException()
            }
        }

        post("/change-passwd") {

        }
    }

}