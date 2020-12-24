package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.routing.exception.UnauthorizedException
import cz.martinforejt.piskvorky.server.security.*
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Route.securityRoutes(jwtManager: JwtManager) {
    val userAuthenticator by inject<IUserAuthenticator>()

    post("/login") {
        val credentials = call.receive<UserCredential>()
        val principal = userAuthenticator.authenticate(credentials)
        principal?.let { call.respond(jwtManager.generateToken(it)) } ?: throw UnauthorizedException()
    }

    post("/register") {

    }

    post("/lost-passwd") {

    }
}