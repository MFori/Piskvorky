package cz.martinforejt.piskvorky.server.routing.exception

import io.ktor.http.*

/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
open class ApiException(
    val httpStatusCode: HttpStatusCode = HttpStatusCode.InternalServerError,
    message: String? = null
) : Exception(message ?: httpStatusCode.description)

class UnauthorizedApiException(message: String? = null) : ApiException(HttpStatusCode.Unauthorized, message)

class ForbiddenApiException(message: String? = null) : ApiException(HttpStatusCode.Forbidden, message)

class AuthenticationApiException : ApiException(HttpStatusCode.Unauthorized, "Invalid login credentials")

class ConflictApiException(message: String) : ApiException(HttpStatusCode.Conflict, message)
