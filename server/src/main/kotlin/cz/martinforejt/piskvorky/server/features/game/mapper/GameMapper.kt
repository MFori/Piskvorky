package cz.martinforejt.piskvorky.server.features.game.mapper

import cz.martinforejt.piskvorky.api.model.GameInvitation
import cz.martinforejt.piskvorky.api.utils.ApiUtils.formatApi
import cz.martinforejt.piskvorky.domain.model.Invitation
import cz.martinforejt.piskvorky.server.core.database.schema.GameInvitationEntity
import cz.martinforejt.piskvorky.server.core.database.schema.GameInvitations
import org.jetbrains.exposed.sql.ResultRow

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun ResultRow.asInvitationDO() = Invitation(
    userId1 = this[GameInvitations.user1].value,
    userId2 = this[GameInvitations.user1].value,
    author = this[GameInvitations.author],
    created = this[GameInvitations.created]
)

fun GameInvitationEntity.toGameInvitation(userId: Int): GameInvitation {
    val rival = if (user1.id.value == userId) user2 else user1
    return GameInvitation(
        userId = rival.id.value,
        email = rival.email,
        created = created.formatApi()
    )
}