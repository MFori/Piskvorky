package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.domain.model.GameResult

/**
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface ResultsRepository {

    suspend fun getResults(): List<GameResult>

    suspend fun getResult(id: Int): GameResult?

    suspend fun addResult(result: GameResult)

    suspend fun editResult(result: GameResult)

    suspend fun deleteResult(id: Int)
}