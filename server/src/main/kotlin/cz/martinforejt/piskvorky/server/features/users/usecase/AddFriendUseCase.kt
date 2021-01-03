package cz.martinforejt.piskvorky.server.features.users.usecase

import cz.martinforejt.piskvorky.api.model.CreateFriendshipRequest
import cz.martinforejt.piskvorky.api.model.FriendShipRequestSocketApiMessage
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
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
class AddFriendUseCase(
    private val friendsRepository: FriendsRepository,
    private val socketServicesManager: SocketServicesManager
) : UseCaseResult<Unit, AddFriendUseCase.Params> {

    override fun execute(params: Params): Result<Unit> {
        val friendship = runBlocking { friendsRepository.getFriendship(params.currentUser.id, params.request.userId) }

        if (friendship == null) {
            runBlocking {
                friendsRepository.createFriendship(params.currentUser.id, params.request.userId)
                socketServicesManager.sendMessageTo(
                    params.request.userId,
                    FriendShipRequestSocketApiMessage(params.currentUser.id, params.currentUser.email, true, false)
                )
            }
        } else if (friendship.author == params.request.userId) {
            runBlocking {
                friendsRepository.confirmFriendship(params.currentUser.id, params.request.userId)

                socketServicesManager.sendMessageTo(
                    params.request.userId,
                    FriendShipRequestSocketApiMessage(params.currentUser.id, params.currentUser.email, false, true)
                )
                socketServicesManager.sendMessageTo(
                    params.currentUser.id,
                    FriendShipRequestSocketApiMessage(params.request.userId, "", false, true)
                )
            }
        }

        return Result(Unit)
    }

    data class Params(
        val currentUser: UserPrincipal,
        val request: CreateFriendshipRequest
    )
}