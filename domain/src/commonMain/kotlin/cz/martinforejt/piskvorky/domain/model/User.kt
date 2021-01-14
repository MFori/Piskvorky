package cz.martinforejt.piskvorky.domain.model

import kotlinx.serialization.Serializable

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/**
 * User DO
 */
expect class User

/**
 * User with password (plain or hashed, depends on specific usage)
 */
expect class UserWithPassword

/**
 * User with token
 */
data class UserWithToken(
    val email: String,
    val token: String
)

/**
 * Public user
 */
@Serializable
data class PublicUser(
    val id: Int,
    val email: String,
    val online: Boolean,
    val inGame: Boolean
)