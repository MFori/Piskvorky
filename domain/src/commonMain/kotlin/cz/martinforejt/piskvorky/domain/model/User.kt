package cz.martinforejt.piskvorky.domain.model

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