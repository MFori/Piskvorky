package cz.martinforejt.piskvorky.domain.model

import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
actual data class User(
    val id: Int,
    val email: String,
    val created: LocalDateTime,
    val admin: Boolean,
    val active: Boolean
)

actual data class UserWithPassword(
    val id: Int,
    val email: String,
    val created: LocalDateTime,
    val admin: Boolean,
    val active: Boolean,
    val password: String
)