package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.domain.model.GameResult

/**
 * Results repository
 *
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface ResultsRepository {

    /**
     * Get all results
     */
    suspend fun getResults(): List<GameResult>

    /**
     * Get result by result id
     */
    suspend fun getResult(id: Int): GameResult?

    /**
     * Add new result
     */
    suspend fun addResult(result: GameResult)

    /**
     * Edit result
     */
    suspend fun editResult(result: GameResult)

    /**
     * Delete results by result id
     */
    suspend fun deleteResult(id: Int)
}