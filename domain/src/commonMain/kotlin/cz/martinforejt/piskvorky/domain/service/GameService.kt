package cz.martinforejt.piskvorky.domain.service

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.usecase.Result

/**
 * Game service (may be used by client apps)
 *
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

interface GameService {

    /**
     * Create/accept game invitation
     */
    suspend fun createInvitation(request: CreateGameRequest, token: String): Result<Unit>

    /**
     * Dismiss game invitation
     */
    suspend fun cancelInvitation(request: CancelGameRequest, token: String): Result<Unit>

    /**
     * Get list of game invitations
     */
    suspend fun getInvitations(token: String): Result<List<GameInvitation>>

    /**
     * Play move
     */
    suspend fun play(request: Move, token: String): Result<Unit>

    /**
     * Give up current game by user [token]
     */
    suspend fun giveUp(token: String): Result<Unit>

    /**
     * Get game by user [token]
     */
    suspend fun getGame(token: String): Result<GameSnap>

}