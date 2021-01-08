package cz.martinforejt.piskvorky.server.features.game.usecase

import cz.martinforejt.piskvorky.api.model.BoardValue
import cz.martinforejt.piskvorky.api.model.GameSnap
import cz.martinforejt.piskvorky.api.model.GameUpdateSocketApiMessage
import cz.martinforejt.piskvorky.api.model.Move
import cz.martinforejt.piskvorky.domain.model.toSnap
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.features.results.usecase.AddGameResultUseCase
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import kotlinx.coroutines.runBlocking

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class PlayMoveUseCase(
    private val gameRepository: GameRepository,
    private val socketService: SocketService,
    private val addGameResultUseCase: AddGameResultUseCase
) : UseCaseResult<Unit, PlayMoveUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        val game = runBlocking { gameRepository.getGame(params.currentUser.id) }
            ?: return Result(error = Error(0, "Not in game."))

        if (game.state != GameSnap.Status.running) {
            return Result(error = Error(0, "Game finished."))
        }

        if (game.current != game.value(params.currentUser.id)) {
            return Result(error = Error(0, "Not on move."))
        }

        if (!game.play(params.currentUser.id, params.request)) {
            return Result(error = Error(0, "Not valid move."))
        }

        if (game.state == GameSnap.Status.end && game.winner != BoardValue.none) {
            addGameResultUseCase.execute(game)
        }

        val message = GameUpdateSocketApiMessage(game.toSnap())
        runBlocking {
            socketService.sendMessageTo(game.cross.id, message)
            socketService.sendMessageTo(game.nought.id, message)
        }

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val request: Move
    )
}