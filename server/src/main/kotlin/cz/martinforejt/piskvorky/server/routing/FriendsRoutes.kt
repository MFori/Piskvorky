package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.repository.FriendsRepository
import cz.martinforejt.piskvorky.server.features.users.mapper.asApiUsers
import cz.martinforejt.piskvorky.server.features.users.usecase.AddFriendUseCase
import cz.martinforejt.piskvorky.server.features.users.usecase.CancelFriendUseCase
import cz.martinforejt.piskvorky.server.routing.exception.ConflictApiException
import cz.martinforejt.piskvorky.server.routing.utils.currentUser
import cz.martinforejt.piskvorky.server.routing.utils.emptyResponse
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/**
 * Friends api routes (protected)
 */
fun Route.friendsRoutes() {
    val friendsRepository by inject<FriendsRepository>()
    val addFriendUseCase by inject<AddFriendUseCase>()
    val cancelFriendUseCase by inject<CancelFriendUseCase>()

    route("/friends") {

        get("/") {
            val users = friendsRepository.getFriends(currentUser.id)
            call.respond(UsersListResponse(users.asApiUsers()))
        }

        get("/requests") {
            val requests = friendsRepository.getRequests(currentUser.id)
            call.respond(FriendsRequestsResponse(requests))
        }

        post("/add") {
            val request = call.receive<CreateFriendshipRequest>()
            val res = addFriendUseCase.execute(AddFriendUseCase.Params(currentUser, request))
            if (res.isSuccessful.not()) {
                throw ConflictApiException(res.error?.message ?: "Error occurred.")
            }
            call.emptyResponse()
        }

        post("/cancel") {
            val request = call.receive<CancelFriendshipRequest>()
            val res = cancelFriendUseCase.execute(CancelFriendUseCase.Params(currentUser, request))
            if (res.isSuccessful.not()) {
                throw ConflictApiException(res.error?.message ?: "Error occurred.")
            }
            call.emptyResponse()
        }
    }

}