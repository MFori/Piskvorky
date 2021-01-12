package cz.martinforejt.piskvorky.server.features.game.usecase

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.toSnap
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.core.service.SocketService
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
    private val usersRepository: UsersRepository,
    private val gameRepository: GameRepository,
    private val socketService: SocketService
) : UseCaseResult<Unit, JoinGameUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        var currentGame = runBlocking { gameRepository.getGame(params.currentUser.id) }
        if (currentGame != null) {
            return Result(error = Error(0, "Already in game"))
        }
        currentGame = runBlocking { gameRepository.getGame(params.request.userId) }
        if (currentGame != null) {
            return Result(error = Error(0, "User already in game"))
        }

        val invitation = runBlocking { gameRepository.getInvitation(params.currentUser.id, params.request.userId) }
        if (invitation == null) {
            runBlocking {
                gameRepository.createInvitation(params.currentUser.id, params.request.userId)

                socketService.sendMessageTo(
                    params.request.userId,
                    GameRequestSocketApiMessage(params.currentUser.id, params.currentUser.email)
                )
            }
        } else if (!LocalDateTime.now().isBefore(invitation.created.plusMinutes(1))) {
            // invitation expired, recreate
            runBlocking {
                gameRepository.updateInvitation(invitation.copy(author = params.currentUser.id))

                socketService.sendMessageTo(
                    params.request.userId,
                    GameRequestSocketApiMessage(params.currentUser.id, params.currentUser.email)
                )
            }
        } else if (invitation.author == params.request.userId) {
            return runBlocking {
                gameRepository.deleteInvitation(params.currentUser.id, params.request.userId)

                val user1 = usersRepository.getUserById(params.currentUser.id)
                    ?: return@runBlocking Result(error = Error(0, "Cant create game"))
                val user2 = usersRepository.getUserById(params.request.userId)
                    ?: return@runBlocking Result(error = Error(0, "Cant create game"))

                val game = gameRepository.newGame(user1, user2)
                    ?: return@runBlocking Result(error = Error(0, "Cant create game"))

                val message = GameUpdateSocketApiMessage(game.toSnap())
                socketService.sendMessageTo(params.request.userId, message)
                socketService.sendMessageTo(params.currentUser.id, message)
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