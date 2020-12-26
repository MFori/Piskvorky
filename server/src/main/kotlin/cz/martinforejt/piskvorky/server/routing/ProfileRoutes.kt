package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.ChangePasswordRequest
import cz.martinforejt.piskvorky.api.model.ProfileInfo
import cz.martinforejt.piskvorky.server.routing.exception.ConflictApiException
import cz.martinforejt.piskvorky.server.routing.exception.UnauthorizedApiException
import cz.martinforejt.piskvorky.server.routing.utils.currentUser
import cz.martinforejt.piskvorky.server.routing.utils.emptyResponse
import cz.martinforejt.piskvorky.server.security.IUserAuthenticator
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

fun Route.profileRoutes() {
    val userAuthenticator by inject<IUserAuthenticator>()


    route("/profile") {

        get("/") {
            call.respond(currentUser.toProfileInfo())
        }

        post("/change-passwd") {
            val request = call.receive<ChangePasswordRequest>()
            userAuthenticator.authenticate(request.toCurrentCredentials())
                ?: throw ConflictApiException("Current password is invalid.")
            // TODO change password
            call.emptyResponse()
        }
    }

}

private fun UserPrincipal.toProfileInfo() = ProfileInfo(email)

private fun ChangePasswordRequest.toCurrentCredentials() = UserCredential(email, passwordCurrent)