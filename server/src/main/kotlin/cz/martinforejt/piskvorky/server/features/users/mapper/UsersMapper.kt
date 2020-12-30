package cz.martinforejt.piskvorky.server.features.users.mapper

import cz.martinforejt.piskvorky.api.model.RegisterRequest
import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.domain.model.UserWithPassword
import cz.martinforejt.piskvorky.server.core.database.Users
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun ResultRow.asUserDO() = User(
    id = this[Users.id].value,
    email = this[Users.email],
    created = this[Users.created],
    admin = this[Users.admin],
    active = this[Users.active]
)

fun ResultRow.asUserWithPassDO() = UserWithPassword(
    id = this[Users.id].value,
    email = this[Users.email],
    created = this[Users.created],
    admin = this[Users.admin],
    active = this[Users.active],
    password = this[Users.password]
)

fun RegisterRequest.toUserWithPassDO() = UserWithPassword(
    id = -1,
    email = this.email,
    created = LocalDateTime.now(),
    admin = false,
    active = true,
    password = this.password
)