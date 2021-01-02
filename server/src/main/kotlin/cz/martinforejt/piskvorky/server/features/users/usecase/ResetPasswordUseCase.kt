package cz.martinforejt.piskvorky.server.features.users.usecase

import cz.martinforejt.piskvorky.api.model.ResetPasswordRequest
import cz.martinforejt.piskvorky.domain.repository.LostPasswordRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.features.users.manager.HashService
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.Unit

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class ResetPasswordUseCase(
    private val usersRepository: UsersRepository,
    private val lostPasswordRepository: LostPasswordRepository,
    private val hashService: HashService
) : UseCaseResult<Unit, ResetPasswordRequest> {

    companion object {
        const val ERROR_EMAIL_INVALID = 1
        const val ERROR_PASSWORD_LENGTH = 2
        const val ERROR_HASH_INVALID = 3
        const val ERROR_EXPIRED = 4
    }

    override fun execute(params: ResetPasswordRequest): Result<Unit> {
        val user = runBlocking { usersRepository.getUserByEmail(params.email) }
            ?: return Result(
                error = Error(ERROR_EMAIL_INVALID, "Invalid email.")
            )
        if (params.password.length < 6) {
            return Result(
                error = Error(ERROR_PASSWORD_LENGTH, "Invalid password length (minimum = 6).")
            )
        }
        val link = runBlocking { lostPasswordRepository.getLink(user.id, params.hash) }
            ?: return Result(
                error = Error(ERROR_HASH_INVALID, "Invalid request.")
            )
        if (!LocalDateTime.now().isBefore(link.created.plusHours(12))) {
            return Result(
                error = Error(ERROR_EXPIRED, "Link expired.")
            )
        }

        val hash = hashService.hashPassword(params.password)
        runBlocking {
            usersRepository.updateUserPassword(user.id, hash)
        }
        return Result(Unit)
    }

}