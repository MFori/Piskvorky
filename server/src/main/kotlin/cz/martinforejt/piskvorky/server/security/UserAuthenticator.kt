package cz.martinforejt.piskvorky.server.security

import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.features.users.manager.HashService
import kotlinx.coroutines.runBlocking


/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class UserAuthenticator(
    private val usersRepository: UsersRepository,
    private val hashService: HashService
) : IUserAuthenticator {

    override fun authenticate(credential: UserCredential): UserPrincipal? {
        val user = runBlocking { usersRepository.getUserWithPasswordByEmail(credential.email) } ?: return null
        val hash = hashService.hashPassword(credential.password)

        return if (hash == user.password) {
            UserPrincipal(user.id, user.email)
        } else {
            null
        }
    }

    override fun authenticate(credential: UserIdCredential): UserPrincipal? {
        return UserPrincipal(credential.id, credential.email)
    }
}