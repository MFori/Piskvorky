package cz.martinforejt.piskvorky.server.features.users.usecase

import cz.martinforejt.piskvorky.api.model.LostPasswordRequest
import cz.martinforejt.piskvorky.domain.repository.LostPasswordRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.domain.service.EmailService
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import io.ktor.server.engine.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import java.net.URLEncoder
import java.util.*

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class LostPasswordUseCase(
    private val usersRepository: UsersRepository,
    private val lostPasswordRepository: LostPasswordRepository,
    private val emailService: EmailService
) : UseCaseResult<Unit, LostPasswordUseCase.Params> {

    companion object {
        private const val KEY = "DKE23K4K2K32KD"
        const val ERROR_EMAIL_INVALID = 1
    }

    @KtorExperimentalAPI
    override fun execute(params: Params): Result<Unit> {
        val user = runBlocking { usersRepository.getUserByEmail(params.request.email) }
            ?: return Result(
                error = Error(ERROR_EMAIL_INVALID, "Invalid email.")
            )

        val hash = URLEncoder.encode(String(Base64.getEncoder().encode(sha1("${user.id}$KEY${System.currentTimeMillis()}".toByteArray()))), "utf-8")
        val url = "${params.webUrl}/reset-password/${params.request.email}/$hash"
        applicationEngineEnvironment { log.debug(url) }

        runBlocking {
            lostPasswordRepository.addLink(user.id, hash)
        }

        //emailService.sendEmail(
        //    "Piskvorky - Reset password",
        //    "Reset link: $url",
        //    arrayOf(user.email)
        //)

        return Result(Unit)
    }

    data class Params(
        val request: LostPasswordRequest,
        val webUrl: String
    )

}