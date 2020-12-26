package cz.martinforejt.piskvorky.server.features.users.repository

import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.domain.model.UserWithPassword
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.core.database.Users
import cz.martinforejt.piskvorky.server.features.users.mapper.asUserDO
import cz.martinforejt.piskvorky.server.features.users.mapper.asUserWithPassDO
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class UsersRepositoryImpl : UsersRepository {

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
}