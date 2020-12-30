package cz.martinforejt.piskvorky.server.features.users.repository

import cz.martinforejt.piskvorky.domain.model.Friendship
import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.server.core.database.Friendships
import cz.martinforejt.piskvorky.server.features.users.mapper.asFriendshipDO
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
class FriendsRepositoryImpl : FriendsRepository {

    override suspend fun deleteFriendship(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        Friendships.deleteWhere {
            (Friendships.user1 eq userId1) and (Friendships.user2 eq userId2)
        }
        // TODO notify via redis
    }

    override suspend fun createFriendship(userId1: Int, userId2: Int): Unit = newSuspendedTransaction {
        Friendships.insert {
            it[user1] = userId1
            it[user2] = userId2
            it[author] = userId1
            it[created] = LocalDateTime.now()
            it[pending] = true
        }

        // TODO notify via redis
    }

    override suspend fun getFriendship(userId1: Int, userId2: Int): Friendship? = newSuspendedTransaction {
        Friendships.select { (Friendships.user1 eq userId1) and (Friendships.user2 eq userId2) }
            .mapNotNull { it.asFriendshipDO() }.singleOrNull()
    }

    override suspend fun getFriends(userId: Int): List<PublicUser> = newSuspendedTransaction {
        listOf(PublicUser(1, "test@test.com", false))
       //UserEntity.find { (Users.id eq userId) }
       //    .map {
       //        it.friends1.map {
       //            it.
       //        }
       //    }

       //FriendshipEntity.find { (Friendships.user1 eq userId) or (Friendships.user2 eq userId) }
       //    .map {
       //        if(it.user1.)
       //    }

        /*
        val friendships = Friendships.select { (Friendships.user1 eq userId) or (Friendships.user2 eq userId) }
            .mapNotNull {
                val frienship = it.asFriendshipDO()
                if(frienship.userId1 == userId) {
                    PublicUser(frienship.)
                } else {

                }
            }*/
    }

}