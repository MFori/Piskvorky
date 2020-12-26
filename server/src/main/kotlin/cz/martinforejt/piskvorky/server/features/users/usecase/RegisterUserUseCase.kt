package cz.martinforejt.piskvorky.server.features.users.usecase

import cz.martinforejt.piskvorky.api.model.RegisterRequest
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCase
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.features.users.manager.HashService
import cz.martinforejt.piskvorky.server.features.users.mapper.toUserWithPassDO
import cz.martinforejt.piskvorky.server.security.IUserAuthenticator
import cz.martinforejt.piskvorky.server.security.UserCredential
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import kotlinx.coroutines.runBlocking

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class RegisterUserUseCase(
    private val usersRepository: UsersRepository,
    private val hashService: HashService
) : UseCaseResult<UserPrincipal, RegisterRequest> {

    companion object {
        const val ERROR_EMAIL_EXISTS = 1
    }

    override fun execute(params: RegisterRequest): Result<UserPrincipal> {
        val user = runBlocking { usersRepository.getUserByEmail(params.email) }
        if (user != null) {
            return Result(
                error = Error(
                    ERROR_EMAIL_EXISTS,
                    "User with this email already exists."
                )
            )
        }
        val hash = hashService.hashPassword(params.password)
        val id = runBlocking { usersRepository.createUser(params.toUserWithPassDO().copy(password = hash)) }
        return Result(UserPrincipal(id, params.email, false))
    }
}