package cz.martinforejt.piskvorky.server.features.users.repository

import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.domain.model.UserWithPassword
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.core.database.schema.Users
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager
import cz.martinforejt.piskvorky.server.features.users.mapper.asUserDO
import cz.martinforejt.piskvorky.server.features.users.mapper.asUserWithPassDO
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class UsersRepositoryImpl(
    private val socketServicesManager: SocketServicesManager
) : UsersRepository {

    override suspend fun getUserById(id: Int): User? = newSuspendedTransaction {
        Users.select { (Users.id eq id) }.mapNotNull { it.asUserDO() }.singleOrNull()
    }

    override suspend fun getUserByEmail(email: String): User? = newSuspendedTransaction {
        Users.select { (Users.email eq email) }.mapNotNull { it.asUserDO() }.singleOrNull()
    }

    override suspend fun getUserWithPasswordByEmail(email: String): UserWithPassword? = newSuspendedTransaction {
        Users.select { (Users.email eq email) }.mapNotNull { it.asUserWithPassDO() }.singleOrNull()
    }

    override suspend fun getUsers(): List<User> = newSuspendedTransaction {
        Users.selectAll().map { it.asUserDO() }
    }

    override suspend fun createUser(user: UserWithPassword): Int = newSuspendedTransaction {
        Users.insertAndGetId {
            it[email] = user.email
            it[password] = user.password
            it[created] = LocalDateTime.now()
            it[admin] = false
            it[active] = true
        }
    }.value

    override suspend fun updateUser(user: User): Unit = newSuspendedTransaction {
        Users.update({ Users.id eq user.id }) {
            it[email] = user.email
            it[admin] = user.admin
            it[active] = user.active
        }
    }

    override suspend fun updateUserPassword(id: Int, passwordHash: String): Unit = newSuspendedTransaction {
        Users.update({ Users.id eq id }) {
            it[password] = passwordHash
        }
    }

    override suspend fun getOnlineUsers(): List<PublicUser> {
        return socketServicesManager.getOnlineUsers()
    }

    override suspend fun isOnline(userId: Int): Boolean {
        return socketServicesManager.isOnline(userId)
    }


    /*
    override suspend fun setOnline(user: PublicUser, online: Boolean) {
        if (online) {
            redis.client.sadd(RedisDatabase.OnlineUsersKey, user.toOnlineJson())
        } else {
            redis.client.srem(RedisDatabase.OnlineUsersKey, user.toOnlineJson())
        }
    }

    override suspend fun getOnlineUsers(): List<PublicUser> {
        return redis.client.smembers(RedisDatabase.OnlineUsersKey).map {
            it.toOnlineUser()
        }.toList()
    }

    override suspend fun isOnline(user: PublicUser): Boolean {
        return redis.client.sismember(RedisDatabase.OnlineUsersKey, user.toOnlineJson())
    }

    private fun PublicUser.toOnlineJson() = Json.encodeToString(PublicUser.serializer(), this.copy(online = true))

    private fun String.toOnlineUser() = Json.decodeFromString(PublicUser.serializer(), this).copy(online = true)
*/
}
