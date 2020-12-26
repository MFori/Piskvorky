package cz.martinforejt.piskvorky.server.security

import io.ktor.auth.*
import kotlinx.serialization.Serializable

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
@Serializable
data class UserCredential(
    val email: String,
    val password: String
) : Credential

@Serializable
data class UserIdCredential(
    val id: Int,
    val email: String,
    val admin: Boolean
) : Credential