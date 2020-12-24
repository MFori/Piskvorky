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
    message: String = httpStatusCode.description
) : Exception(message)

class UnauthorizedException : ApiException(HttpStatusCode.Unauthorized)

class AuthenticationException : ApiException(HttpStatusCode.Unauthorized, "Invalid login credentials")