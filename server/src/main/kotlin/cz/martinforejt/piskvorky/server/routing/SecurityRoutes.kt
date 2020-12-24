package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.routing.exception.UnauthorizedException
import cz.martinforejt.piskvorky.server.security.JwtConfig
import cz.martinforejt.piskvorky.server.security.UserCredential
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Route.securityRoutes(jwtConfig: JwtConfig) {

    post("/login") {
        val credentials = call.receive<UserCredential>()

        if(credentials.email=="admin@admin.com" && credentials.password=="pass123") {
            call.respond(jwtConfig.generateToken(UserPrincipal(1, credentials.email)))
        } else {
            throw UnauthorizedException()
        }
    }

    post("/register") {

    }

    post("/lost-passwd") {

    }
}