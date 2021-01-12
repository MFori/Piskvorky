package cz.martinforejt.piskvorky.server.features.admin.usecase

import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.features.admin.model.EditUserRequest
import cz.martinforejt.piskvorky.server.features.users.manager.HashService
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import kotlinx.coroutines.runBlocking

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class EditUserUseCase(
    private val usersRepository: UsersRepository,
    private val hashService: HashService
) : UseCaseResult<Unit, EditUserUseCase.Params> {

    companion object {
        const val ERROR_PASSWORDS_NOT_MATCH = 1
        const val ERROR_PASSWORD_LENGTH = 2
    }

    override fun execute(params: Params): Result<Unit> {
        if(params.request.delete) {
            if(params.user.id == params.currentUser.id) {
                return Result(
                    error = Error(0, "Can not delete self account!")
                )
            }
            runBlocking { usersRepository.deleteUser(params.user.id) }
        } else {
            val updated = params.user.copy(
                admin = params.request.admin,
                active = params.request.active
            )

            runBlocking { usersRepository.updateUser(updated) }

            if(params.request.pass.isNullOrBlank().not() && params.request.passConfirm.isNullOrBlank().not()) {
                if(params.request.pass != params.request.passConfirm) {
                    return Result(
                        error = Error(ERROR_PASSWORDS_NOT_MATCH, "Passwords do not match.")
                    )
                }
                if(params.request.pass!!.length < 6) {
                    return Result(
                        error = Error(ERROR_PASSWORD_LENGTH, "Invalid password length (minimum = 6).")
                    )
                }
                val hash = hashService.hashPassword(params.request.pass)
                runBlocking {
                    usersRepository.updateUserPassword(updated.id, hash)
                }
            }
        }

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val user: User,
        val request: EditUserRequest
    )
}