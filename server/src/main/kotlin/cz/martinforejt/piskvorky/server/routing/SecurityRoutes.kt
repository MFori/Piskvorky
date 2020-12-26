package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.LoginRequest
import cz.martinforejt.piskvorky.api.model.LoginResponse
import cz.martinforejt.piskvorky.api.model.LostPasswordRequest
import cz.martinforejt.piskvorky.api.model.RegisterRequest
import cz.martinforejt.piskvorky.server.features.users.usecase.RegisterUserUseCase
import cz.martinforejt.piskvorky.server.features.users.usecase.ValidateUserCredentialsUseCase
import cz.martinforejt.piskvorky.server.routing.exception.ConflictApiException
import cz.martinforejt.piskvorky.server.routing.exception.UnauthorizedApiException
import cz.martinforejt.piskvorky.server.routing.utils.emptyResponse
import cz.martinforejt.piskvorky.server.security.JwtManager
import cz.martinforejt.piskvorky.server.security.UserCredential
import cz.martinforejt.piskvorky.server.security.UserPrincipal
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
    val validateCredentialsUSeCase by inject<ValidateUserCredentialsUseCase>()
    val registerUserUseCase by inject<RegisterUserUseCase>()

    suspend fun ApplicationCall.createTokenResponse(principal: UserPrincipal) {
        val token = jwtManager.generateToken(principal)
        respond(LoginResponse(token))
    }

    post("/login") {
        val request = call.receive<LoginRequest>()
        val principal = validateCredentialsUSeCase.execute(request.toCredentials()) ?: throw UnauthorizedApiException()
        application.environment.log.info("Logged user")
        call.createTokenResponse(principal)
    }

    post("/register") {
        val request = call.receive<RegisterRequest>()
        val res = registerUserUseCase.execute(request)
        if (res.isSuccessful.not()) {
            throw ConflictApiException(res.error?.message ?: "Error occurred.")
        }
        call.createTokenResponse(res.data!!)
    }

    post("/lost-passwd") {
        val request = call.receive<LostPasswordRequest>()
        // TODO find user and send email
        call.emptyResponse()
    }
}

private fun LoginRequest.toCredentials() = UserCredential(email, password)