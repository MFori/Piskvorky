package cz.martinforejt.piskvorky.server.features.users.usecase

import cz.martinforejt.piskvorky.api.model.CancelFriendshipRequest
import cz.martinforejt.piskvorky.api.model.FriendshipCancelledSocketApiMessage
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import cz.martinforejt.piskvorky.domain.usecase.UseCaseResult
import cz.martinforejt.piskvorky.server.core.service.SocketServicesManager
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import kotlinx.coroutines.runBlocking

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class CancelFriendUseCase(
    private val friendsRepository: FriendsRepository,
    private val socketServicesManager: SocketServicesManager
) : UseCaseResult<Unit, CancelFriendUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        runBlocking { friendsRepository.getFriendship(params.currentUser.id, params.request.userId) }
            ?: return Result(
                error = Error(0, "Friendship not exists.")
            )

        runBlocking {
            friendsRepository.deleteFriendship(params.currentUser.id, params.request.userId)

            socketServicesManager.sendMessageTo(
                params.request.userId,
                FriendshipCancelledSocketApiMessage(params.currentUser.id, params.currentUser.email)
            )
        }

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val request: CancelFriendshipRequest
    )
}