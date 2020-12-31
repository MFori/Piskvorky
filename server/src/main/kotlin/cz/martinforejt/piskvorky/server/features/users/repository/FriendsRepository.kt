package cz.martinforejt.piskvorky.server.features.users.repository

import cz.martinforejt.piskvorky.domain.model.Friendship
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.core.database.Friendships
import cz.martinforejt.piskvorky.server.core.database.UserEntity
import cz.martinforejt.piskvorky.server.features.users.mapper.asFriendshipDO
import cz.martinforejt.piskvorky.server.features.users.mapper.asPublicUser
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class FriendsRepositoryImpl(
    private val usersRepository: UsersRepository
) : FriendsRepository {

    override suspend fun deleteFriendship(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        Friendships.deleteWhere {
            (Friendships.user1 eq userId1) and (Friendships.user2 eq userId2)
        }
    }

    override suspend fun createFriendship(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        Friendships.insert {
            it[user1] = userId1
            it[user2] = userId2
            it[author] = userId1
            it[created] = LocalDateTime.now()
            it[pending] = true
        }
    }

    override suspend fun getFriendship(userId1: Int, userId2: Int): Friendship? = newSuspendedTransaction {
        Friendships.select { (Friendships.user1 eq userId1) and (Friendships.user2 eq userId2) }
            .mapNotNull { it.asFriendshipDO() }.singleOrNull()
    }

    override suspend fun getFriends(userId: Int): List<PublicUser> = newSuspendedTransaction {
        val friends = UserEntity.findById(userId)
            ?.friends ?: emptyList()

        friends.map { entity -> entity.asPublicUser().let { it.copy(online = usersRepository.isOnline(it)) } }
    }

}