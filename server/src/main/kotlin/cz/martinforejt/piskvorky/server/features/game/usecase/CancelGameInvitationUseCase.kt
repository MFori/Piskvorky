package cz.martinforejt.piskvorky.server.features.game.usecase

import cz.martinforejt.piskvorky.api.model.CancelGameRequest
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import kotlinx.coroutines.runBlocking

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class CancelGameInvitationUseCase(
    private val gameRepository: GameRepository
) : UseCaseResult<Unit, CancelGameInvitationUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        runBlocking { gameRepository.getInvitation(params.currentUser.id, params.request.userId) }
            ?:  return Result(
                error = Error(0, "Invitation not exists.")
            )

        runBlocking {
            gameRepository.deleteInvitation(params.currentUser.id, params.request.userId)
        }

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val request: CancelGameRequest
    )
}