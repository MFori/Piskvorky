package cz.martinforejt.piskvorky.server.features.users.mapper

import cz.martinforejt.piskvorky.domain.model.LostPasswordLink
import cz.martinforejt.piskvorky.server.core.database.schema.LostPasswords
import org.jetbrains.exposed.sql.ResultRow

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun ResultRow.asLostPasswordLink() = LostPasswordLink(
    userId = this[LostPasswords.user].value,
    link = this[LostPasswords.link],
    created = this[LostPasswords.created]
)