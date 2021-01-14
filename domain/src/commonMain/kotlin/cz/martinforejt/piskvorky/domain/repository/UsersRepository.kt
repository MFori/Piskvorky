package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.domain.model.PublicUser
import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.domain.model.UserWithPassword

/**
 * Users repository
 *
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface UsersRepository {

    suspend fun getUserById(id: Int): User?

    suspend fun getUserByEmail(email: String): User?

    /**
     * Get user with password (hashed) by [email]
     */
    suspend fun getUserWithPasswordByEmail(email: String): UserWithPassword?

    suspend fun getUsers(): List<User>

    /**
     * Create user
     *
     * @param user new user, with hashed password
     * @return userId of created user
     */
    suspend fun createUser(user: UserWithPassword): Int

    suspend fun updateUser(user: User)

    /**
     * Update user password
     *
     * @param id userId
     * @param passwordHash hashed password
     */
    suspend fun updateUserPassword(id: Int, passwordHash: String)

    /**
     * Get list of current online users
     */
    suspend fun getOnlineUsers(): List<PublicUser>

    /**
     * Check if user with [userId] is online
     */
    suspend fun isOnline(userId: Int): Boolean

    suspend fun deleteUser(userId: Int)

}