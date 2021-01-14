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
import io.ktor.response.*
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

//const val BASIC_AUTH_ADMIN = "basic-auth-admin"
const val FORM_AUTH_ADMIN = "form-auth-admin"
const val SESSION_AUTH_ADMIN = "session-auth-admin"

@KtorExperimentalAPI
fun Application.setUpSecurity() {

    val jwtConfig by inject<JwtManager>()
    val validateCredentialsUseCase by inject<ValidateUserCredentialsUseCase>()

    install(Authentication) {
        // Jwt authentication for public api
        jwt(JWT_AUTH_USER) {
            realm = jwtConfig.realm
            verifier(jwtConfig.verifier)
            validate { credential ->
                jwtConfig.validateCredential(credential) ?: throw AuthenticationApiException()
            }
            challenge { _, _ -> throw UnauthorizedApiException() }
        }
        // Jwt authentication for admin api
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
        // Htt basic authentication for admin
        /*basic(BASIC_AUTH_ADMIN) {
            realm = jwtConfig.realm
            validate { credential ->
                val principal = validateCredentialsUseCase.execute(credential.toUserCredential())
                    ?: throw AuthenticationApiException()
                if (principal.admin.not()) {
                    throw ForbiddenApiException()
                }
                principal
            }
        }*/
        // Form authentication for admin
        form(FORM_AUTH_ADMIN) {
            challenge {
                @Suppress("DEPRECATION")
                val errors: Map<Any, AuthenticationFailedCause> = call.authentication.errors
                when (errors.values.singleOrNull()) {
                    AuthenticationFailedCause.InvalidCredentials ->
                        call.respondRedirect("/login?invalid")

                    AuthenticationFailedCause.NoCredentials ->
                        call.respondRedirect("/login?no")

                    else ->
                        call.respondRedirect("/login")
                }

            }
            validate { credential ->
                val principal = validateCredentialsUseCase.execute(credential.toUserCredential())
                    ?: return@validate null
                if (principal.admin.not()) {
                    return@validate null
                }
                principal
            }
        }
        // Session authentication for admin (created after form authentication)
        session<UserPrincipal>(SESSION_AUTH_ADMIN) {
            challenge("/login")
            validate { session: UserPrincipal -> session }
        }
    }

    routing {
        route(API_VERSION) {
            securityRoutes(jwtConfig)
        }
    }
}

private fun UserPasswordCredential.toUserCredential() = UserCredential(name, password)