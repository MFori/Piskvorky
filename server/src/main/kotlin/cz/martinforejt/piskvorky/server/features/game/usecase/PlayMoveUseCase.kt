package cz.martinforejt.piskvorky.server.features.game.usecase

import cz.martinforejt.piskvorky.api.model.Move
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager
import cz.martinforejt.piskvorky.server.security.UserPrincipal

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class PlayMoveUseCase(
    private val gameRepository: GameRepository,
    private val socketServicesManager: SocketServicesManager
) : UseCaseResult<Unit, PlayMoveUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        // TODO get game, make move, validate, send notifications

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val request: Move
    )
}