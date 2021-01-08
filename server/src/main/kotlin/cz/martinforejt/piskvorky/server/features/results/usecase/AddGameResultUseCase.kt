package cz.martinforejt.piskvorky.server.features.results.usecase

import cz.martinforejt.piskvorky.api.model.BoardValue
import cz.martinforejt.piskvorky.api.model.GameSnap
import cz.martinforejt.piskvorky.domain.model.Game
import cz.martinforejt.piskvorky.domain.model.GameResult
import cz.martinforejt.piskvorky.domain.repository.ResultsRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class AddGameResultUseCase(
    private val usersRepository: UsersRepository,
    private val resultsRepository: ResultsRepository
) : UseCaseResult<Unit, Game> {

    override fun execute(params: Game): Result<Unit> {
        if (params.state != GameSnap.Status.end || params.winner == BoardValue.none) {
            return Result(error = Error(0, "Game not finished"))
        }

        val user1 = runBlocking { usersRepository.getUserById(params.cross.id) }!!
        val user2 = runBlocking { usersRepository.getUserById(params.nought.id) }!!

        val winner = if (params.winner == BoardValue.cross) {
            user1.id
        } else {
            user2.id
        }

        val result = GameResult(
            user1 = user1,
            user2 = user2,
            created = LocalDateTime.now(),
            winnerId = winner
        )

        runBlocking {
            resultsRepository.addResult(result)
        }

        return Result(Unit)
    }
}