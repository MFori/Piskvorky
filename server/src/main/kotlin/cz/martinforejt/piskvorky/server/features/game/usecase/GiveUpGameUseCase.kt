package cz.martinforejt.piskvorky.server.features.game.usecase

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.toSnap
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
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
class GiveUpGameUseCase(
    private val gameRepository: GameRepository,
    private val usersRepository: UsersRepository,
    private val socketService: SocketService,
    private val addGameResultUseCase: AddGameResultUseCase
) : UseCaseResult<Unit, GiveUpGameUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        val game = runBlocking { gameRepository.getGame(params.currentUser.id) }
            ?: return Result(error = Error(0, "Not in game."))

        if (game.state == GameSnap.Status.running) {
            game.giveUp(params.currentUser.id)
            addGameResultUseCase.execute(game)

            val message = GameUpdateSocketApiMessage(game.toSnap())
            runBlocking {
                socketService.sendMessageTo(game.cross.id, message)
                socketService.sendMessageTo(game.nought.id, message)
            }
        }

        runBlocking {
            gameRepository.removeGame(params.currentUser.id)
            val users = usersRepository.getOnlineUsers()
            socketService.sendBroadcast(SocketApi.encode(OnlineUsersSocketApiMessage(users)))
        }

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal
    )
}