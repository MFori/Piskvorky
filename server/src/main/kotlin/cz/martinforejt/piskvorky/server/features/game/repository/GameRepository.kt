package cz.martinforejt.piskvorky.server.features.game.repository

import cz.martinforejt.piskvorky.api.model.BoardValue
import cz.martinforejt.piskvorky.api.model.GameInvitation
import cz.martinforejt.piskvorky.api.model.Player
import cz.martinforejt.piskvorky.domain.model.Game
import cz.martinforejt.piskvorky.domain.model.Invitation
import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.server.core.database.schema.GameInvitationEntity
import cz.martinforejt.piskvorky.server.core.database.schema.GameInvitations
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.features.game.mapper.asInvitationDO
import cz.martinforejt.piskvorky.server.features.game.mapper.toGameInvitation
import cz.martinforejt.piskvorky.server.features.game.mapper.toPlayer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class GameRepositoryImpl : GameRepository {

    private val games: MutableMap<Int, Game> = ConcurrentHashMap<Int, Game>()

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
        return games[userId]
    }

    override suspend fun newGame(user1: User, user2: User): Game? {
        val r = Random.nextBoolean()
        val cross = if (r) user1.toPlayer(true) else user2.toPlayer(false)
        val nought = if (r) user2.toPlayer(true) else user1.toPlayer(false)

        val game = Game(
            cross = cross,
            nought = nought,
            current = BoardValue.cross
        )

        games[user1.id] = game
        games[user2.id] = game

        return game
    }

}