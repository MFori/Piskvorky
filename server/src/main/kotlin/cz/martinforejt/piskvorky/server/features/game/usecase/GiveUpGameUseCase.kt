package cz.martinforejt.piskvorky.server.features.game.usecase

import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.security.UserPrincipal

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class GiveUpGameUseCase(
    private val gameRepository: GameRepository,
    private val socketService: SocketService
) : UseCaseResult<Unit, GiveUpGameUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        // TODO get game, make giveup, send notifications

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal
    )
}