package cz.martinforejt.piskvorky.domain.service

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.usecase.Result

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

interface GameService {

    suspend fun createInvitation(request: CreateGameRequest, token: String): Result<Unit>

    suspend fun cancelInvitation(request: CancelGameRequest, token: String): Result<Unit>

    suspend fun getInvitations(token: String): Result<List<GameInvitation>>

    suspend fun play(request: Move, token: String): Result<Unit>

    suspend fun giveUp(token: String): Result<Unit>

    suspend fun getGame(token: String): Result<GameSnap>

}