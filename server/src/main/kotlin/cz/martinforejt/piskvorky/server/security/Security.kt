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

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

const val JWT_AUTH_NAME = "jwt-auth"

@KtorExperimentalAPI
fun Application.setUpSecurity() {

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtValidity = environment.config.property("jwt.validity_ms").getString().toInt()

    val jwtConfig = JwtConfig(jwtIssuer, jwtSecret, jwtValidity)

    install(Authentication) {
        jwt(JWT_AUTH_NAME) {
            realm = jwtRealm
            verifier(jwtConfig.verifier)
            validate { credential -> validateCredential(credential) }
            challenge { _, _ -> throw UnauthorizedException() }
        }
    }

    routing {
        route(API_VERSION) {
            securityRoutes(jwtConfig)
        }
    }
}

private fun validateCredential(credential: JWTCredential): Principal? =
    if (credential.payload.getClaim("id").isNull || credential.payload.getClaim("email").isNull) {
        throw AuthenticationException()
    } else {
        JWTPrincipal(credential.payload)
    }
