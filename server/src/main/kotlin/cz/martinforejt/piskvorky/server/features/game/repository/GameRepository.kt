package cz.martinforejt.piskvorky.server.features.game.repository

import cz.martinforejt.piskvorky.api.model.GameInvitation
import cz.martinforejt.piskvorky.domain.model.Game
import cz.martinforejt.piskvorky.domain.model.Invitation
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.server.core.database.schema.GameInvitationEntity
import cz.martinforejt.piskvorky.server.core.database.schema.GameInvitations
import cz.martinforejt.piskvorky.server.features.game.mapper.asInvitationDO
import cz.martinforejt.piskvorky.server.features.game.mapper.toGameInvitation
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class GameRepositoryImpl : GameRepository {

    override suspend fun deleteInvitation(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        GameInvitations.deleteWhere {
            ((GameInvitations.user1 eq userId1) and (GameInvitations.user2 eq userId2)) or
                    ((GameInvitations.user1 eq userId2) and (GameInvitations.user2 eq userId1))
        }
    }

    override suspend fun createInvitation(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        GameInvitations.insert {
            it[user1] = userId1
            it[user2] = userId2
            it[author] = userId1
            it[created] = LocalDateTime.now()
        }
    }

    override suspend fun updateInvitation(invitation: Invitation): Unit = newSuspendedTransaction {
        GameInvitations.update({
            ((GameInvitations.user1 eq invitation.userId1) and (GameInvitations.user2 eq invitation.userId2)) or
                    ((GameInvitations.user1 eq invitation.userId2) and (GameInvitations.user2 eq invitation.userId1))
        }) {
            it[author] = invitation.author
            it[created] = LocalDateTime.now()
        }
    }

    override suspend fun getInvitation(userId1: Int, userId2: Int): Invitation? = newSuspendedTransaction {
        GameInvitations.select {
            ((GameInvitations.user1 eq userId1) and (GameInvitations.user2 eq userId2)) or
                    ((GameInvitations.user1 eq userId2) and (GameInvitations.user2 eq userId1))
        }.mapNotNull { it.asInvitationDO() }.singleOrNull()
    }

    override suspend fun getInvitations(userId: Int): List<GameInvitation> = newSuspendedTransaction {
        GameInvitationEntity.find {
            ((GameInvitations.user1 eq userId) or (GameInvitations.user2 eq userId))
        }.map { it.toGameInvitation(userId) }
    }

    override suspend fun getGame(userId: Int): Game? {
        TODO("Not yet implemented")
    }

    override suspend fun newGame(userId1: Int, userId2: Int): Game? {
        TODO("Not yet implemented")
    }

}