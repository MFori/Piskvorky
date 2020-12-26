package cz.martinforejt.piskvorky.domain.model

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
data class User(
    val id: Int,
    val email: String
)

data class UserWithPassword(
    val id: Int,
    val email: String,
    val password: String
)