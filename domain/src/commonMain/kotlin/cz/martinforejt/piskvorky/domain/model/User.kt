package cz.martinforejt.piskvorky.domain.model

import kotlinx.serialization.Serializable

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
expect class User

expect class UserWithPassword

data class UserWithToken(
    val email: String,
    val token: String
)

@Serializable
data class PublicUser(
    val id: Int,
    val email: String,
    val online: Boolean
)