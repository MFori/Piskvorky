package cz.martinforejt.piskvorky.server.features.users.usecase

import cz.martinforejt.piskvorky.api.model.ChangePasswordRequest
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.features.users.manager.HashService
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
class ChangePasswordUseCase(
    private val usersRepository: UsersRepository,
    private val authenticator: IUserAuthenticator,
    private val hashService: HashService
) : UseCaseResult<Unit, ChangePasswordUseCase.Params> {

    companion object {
        const val ERROR_EMAIL_INVALID = 1
        const val ERROR_INVALID_PASSWORD = 2
        const val ERROR_PASSWORD_LENGTH = 3
    }

    override fun execute(params: Params): Result<Unit> {
        if (params.currentUser.email != params.request.email) {
            return Result(
                error = Error(ERROR_EMAIL_INVALID, "Invalid email.")
            )
        }
        if (authenticator.authenticate(params.request.toUserCredential()) == null) {
            return Result(
                error = Error(ERROR_INVALID_PASSWORD, "Current password invalid.")
            )
        }
        if (params.request.password.length < 6) {
            return Result(
                error = Error(ERROR_PASSWORD_LENGTH, "Invalid password length (minimum = 6).")
            )
        }
        val hash = hashService.hashPassword(params.request.password)
        runBlocking {
            usersRepository.updateUserPassword(params.currentUser.id, hash)
        }
        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val request: ChangePasswordRequest
    )

    private fun ChangePasswordRequest.toUserCredential() = UserCredential(email, passwordCurrent)
}