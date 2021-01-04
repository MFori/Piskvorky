package cz.martinforejt.piskvorky.server.features.users.repository

import cz.martinforejt.piskvorky.api.model.FriendRequest
import cz.martinforejt.piskvorky.domain.model.Friendship
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.core.database.schema.FriendshipEntity
import cz.martinforejt.piskvorky.server.core.database.schema.FriendshipId
import cz.martinforejt.piskvorky.server.core.database.schema.Friendships
import cz.martinforejt.piskvorky.server.core.database.schema.UserEntity
import cz.martinforejt.piskvorky.server.features.users.mapper.asFriendshipDO
import cz.martinforejt.piskvorky.server.features.users.mapper.asPublicUser
import cz.martinforejt.piskvorky.server.features.users.mapper.toFriendRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class FriendsRepositoryImpl(
    private val usersRepository: UsersRepository,
    private val gameRepository: GameRepository
) : FriendsRepository {

    override suspend fun deleteFriendship(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        Friendships.deleteWhere {
            ((Friendships.user1 eq userId1) and (Friendships.user2 eq userId2)) or
                    ((Friendships.user1 eq userId2) and (Friendships.user2 eq userId1))
        }
    }

    override suspend fun createFriendship(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        Friendships.insert {
            it[user1] = userId1
            it[user2] = userId2
            it[author] = userId1
            it[created] = LocalDateTime.now()
            it[pending] = true
            it[id] = FriendshipId(userId1, userId2)
        }
    }

    override suspend fun confirmFriendship(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        Friendships.update({
            ((Friendships.user1 eq userId1) and (Friendships.user2 eq userId2)) or
                    ((Friendships.user1 eq userId2) and (Friendships.user2 eq userId1))
        }) {
            it[pending] = false
        }
    }

    override suspend fun getFriendship(userId1: Int, userId2: Int): Friendship? = newSuspendedTransaction {
        Friendships.select {
            ((Friendships.user1 eq userId1) and (Friendships.user2 eq userId2)) or
                    ((Friendships.user1 eq userId2) and (Friendships.user2 eq userId1))
        }.mapNotNull { it.asFriendshipDO() }.singleOrNull()
    }

    override suspend fun getFriends(userId: Int): List<PublicUser> = newSuspendedTransaction {
        val friends = UserEntity.findById(userId)
            ?.friends ?: emptyList()
        friends.map { entity ->
            entity.asPublicUser().let {
                it.copy(
                    online = usersRepository.isOnline(it.id),
                    inGame = gameRepository.getGame(it.id) != null
                )
            }
        }
    }

    override suspend fun getRequests(userId: Int): List<FriendRequest> = newSuspendedTransaction {
        FriendshipEntity.find {
            ((Friendships.user1 eq userId) or (Friendships.user2 eq userId)) and Friendships.pending.eq(
                false
            )
        }.map { it.toFriendRequest(userId) }
    }
}