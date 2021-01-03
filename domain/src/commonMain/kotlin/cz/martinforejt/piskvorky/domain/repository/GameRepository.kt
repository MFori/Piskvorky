package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.api.model.GameInvitation
import cz.martinforejt.piskvorky.domain.model.Game
import cz.martinforejt.piskvorky.domain.model.Invitation

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface GameRepository {

    suspend fun deleteInvitation(userId1: Int, userId2: Int)

    suspend fun createInvitation(userId1: Int, userId2: Int)

    suspend fun updateInvitation(invitation: Invitation)

    suspend fun getInvitation(userId1:Int, userId2:Int) : Invitation?

    suspend fun getInvitations(userId: Int) : List<GameInvitation>

    suspend fun getGame(userId: Int) : Game?

    suspend fun newGame(userId1: Int, userId2: Int) : Game?
}