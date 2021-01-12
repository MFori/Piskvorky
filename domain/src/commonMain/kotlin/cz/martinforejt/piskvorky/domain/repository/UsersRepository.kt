package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.domain.model.UserWithPassword

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface UsersRepository {

    suspend fun getUserById(id: Int): User?

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserWithPasswordByEmail(email: String): UserWithPassword?

    suspend fun getUsers(): List<User>

    suspend fun createUser(user: UserWithPassword): Int

    suspend fun updateUser(user: User)

    suspend fun updateUserPassword(id: Int, passwordHash: String)

    suspend fun getOnlineUsers(): List<PublicUser>

    suspend fun isOnline(userId: Int): Boolean

    suspend fun deleteUser(userId: Int)

}