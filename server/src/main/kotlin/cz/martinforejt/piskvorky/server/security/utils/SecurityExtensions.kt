package cz.martinforejt.piskvorky.server.security.utils

import com.auth0.jwt.interfaces.Payload
import cz.martinforejt.piskvorky.server.security.UserIdCredential
import io.ktor.auth.jwt.*

/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Payload.asStringOrNull(name: String): String? {
    val claim = this.getClaim(name)
    return if (claim.isNull) {
        null
    } else {
        claim.asString()
    }
}

fun Payload.asIntOrNull(name: String): Int? {
    val claim = this.getClaim(name)
    return if (claim.isNull) {
        null
    } else {
        claim.asInt()
    }
}

fun Payload.asBoolOrNull(name: String): Boolean? {
    val claim = this.getClaim(name)
    return if (claim.isNull) {
        null
    } else {
        claim.asBoolean()
    }
}

fun JWTCredential.toUserIDCredential(): UserIdCredential? {
    val id = payload.asIntOrNull("id") ?: return null
    val email = payload.asStringOrNull("email") ?: return null
    val admin = payload.asBoolOrNull("admin") ?: return null
    return UserIdCredential(id, email, admin)
}