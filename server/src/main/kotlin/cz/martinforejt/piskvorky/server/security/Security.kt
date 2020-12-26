package cz.martinforejt.piskvorky.server.security

import cz.martinforejt.piskvorky.server.features.users.usecase.ValidateUserCredentialsUseCase
import cz.martinforejt.piskvorky.server.routing.API_VERSION
import cz.martinforejt.piskvorky.server.routing.exception.AuthenticationApiException
import cz.martinforejt.piskvorky.server.routing.exception.ForbiddenApiException
import cz.martinforejt.piskvorky.server.routing.exception.UnauthorizedApiException
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

const val JWT_AUTH_USER = "jwt-auth-user"
const val JWT_AUTH_ADMIN = "jwt-auth-admin"
const val BASIC_AUTH_ADMIN = "basic-auth-admin"

@KtorExperimentalAPI
fun Application.setUpSecurity() {

    val jwtConfig by inject<JwtManager>()
    val validateCredentialsUseCase by inject<ValidateUserCredentialsUseCase>()

    install(Authentication) {
        jwt(JWT_AUTH_USER) {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                jwtConfig.validateCredential(credential) ?: throw AuthenticationApiException()
            }
            challenge { _, _ -> throw UnauthorizedApiException() }
        }
        jwt(JWT_AUTH_ADMIN) {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                val principal = jwtConfig.validateCredential(credential) ?: throw AuthenticationApiException()
                if (principal.admin.not()) {
                    throw ForbiddenApiException()
                }
                principal
            }
            challenge { _, _ -> throw UnauthorizedApiException() }
        }
        basic(BASIC_AUTH_ADMIN) {
            realm = jwtConfig.realm
            validate { credential ->
                val principal = validateCredentialsUseCase.execute(credential.toUserCredential())
                    ?: throw AuthenticationApiException()
                if (principal.admin.not()) {
                    throw ForbiddenApiException()
                }
                principal
            }
        }
    }

    routing {
        route(API_VERSION) {
            securityRoutes(jwtConfig)
        }
    }
}

private fun UserPasswordCredential.toUserCredential() = UserCredential(name, password)