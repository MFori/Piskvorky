package cz.martinforejt.piskvorky.server.routing.utils

import cz.martinforejt.piskvorky.api.model.Error
import cz.martinforejt.piskvorky.server.routing.exception.ApiException
import cz.martinforejt.piskvorky.server.routing.exception.UnauthorizedApiException
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

/**
 * Created by Martin Forejt on 25.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

suspend fun ApplicationCall.emptyResponse(status: HttpStatusCode = HttpStatusCode.OK) {
    this.respond(status, "")
}

suspend fun ApplicationCall.errorResponse(cause: Throwable, status: HttpStatusCode = HttpStatusCode.OK) {
    if (cause is ApiException) {
        errorResponse(cause)
    } else {
        errorResponse(ApiException(status, cause.message ?: cause.localizedMessage))
    }
}

suspend fun ApplicationCall.errorResponse(cause: ApiException) {
    this.respond(
        message = Error(cause.httpStatusCode.value, cause.message ?: cause.localizedMessage),
        status = cause.httpStatusCode
    )
}

val PipelineContext<*, ApplicationCall>.currentUser get() = call.currentUser()

fun ApplicationCall.currentUser() = principal<UserPrincipal>() ?: throw UnauthorizedApiException()