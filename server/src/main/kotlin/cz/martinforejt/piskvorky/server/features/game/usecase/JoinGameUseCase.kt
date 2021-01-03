package cz.martinforejt.piskvorky.server.features.game.usecase

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.toSnap
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class JoinGameUseCase(
    private val gameRepository: GameRepository,
    private val socketServicesManager: SocketServicesManager
) : UseCaseResult<Unit, JoinGameUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        runBlocking { gameRepository.getGame(params.currentUser.id) }
            ?: return Result(error = Error(0, "Already in game"))

        val invitation = runBlocking { gameRepository.getInvitation(params.currentUser.id, params.request.userId) }
        if (invitation == null) {
            runBlocking {
                gameRepository.createInvitation(params.currentUser.id, params.request.userId)

                socketServicesManager.sendMessageTo(
                    params.request.userId,
                    GameRequestSocketApiMessage(params.currentUser.id, params.currentUser.email)
                )
            }
        } else if (!LocalDateTime.now().isBefore(invitation.created.plusMinutes(1))) {
            // invitation expired, recreate
            runBlocking {
                gameRepository.updateInvitation(invitation.copy(author = params.currentUser.id))

                socketServicesManager.sendMessageTo(
                    params.request.userId,
                    GameRequestSocketApiMessage(params.currentUser.id, params.currentUser.email)
                )
            }
        } else if (invitation.author == params.request.userId) {
            return runBlocking {
                gameRepository.deleteInvitation(params.currentUser.id, params.request.userId)

                val game = gameRepository.newGame(params.currentUser.id, params.request.userId)
                    ?: return@runBlocking Result(error = Error(0, "Cant create game"))

                val message = GameUpdateSocketApiMessage(game.toSnap())
                socketServicesManager.sendMessageTo(params.request.userId, message)
                socketServicesManager.sendMessageTo(params.currentUser.id, message)
                Result(Unit)
            }
        }

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val request: CreateGameRequest
    )
}