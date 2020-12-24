package cz.martinforejt.piskvorky.server.security

import cz.martinforejt.piskvorky.server.routing.API_VERSION
import cz.martinforejt.piskvorky.server.routing.exception.AuthenticationException
import cz.martinforejt.piskvorky.server.routing.exception.UnauthorizedException
import cz.martinforejt.piskvorky.server.routing.securityRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.routing.*
import io.ktor.util.*
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

const val JWT_AUTH_NAME = "jwt-auth"

@KtorExperimentalAPI
fun Application.setUpSecurity() {

    val jwtConfig by inject<JwtManager>()

    install(Authentication) {
        jwt(JWT_AUTH_NAME) {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                jwtConfig.validateCredential(credential) ?: throw AuthenticationException()
            }
            challenge { _, _ -> throw UnauthorizedException() }
        }
    }

    routing {
        route(API_VERSION) {
            securityRoutes(jwtConfig)
        }
    }
}