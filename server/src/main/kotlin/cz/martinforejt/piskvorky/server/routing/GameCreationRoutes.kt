package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.CancelGameRequest
import cz.martinforejt.piskvorky.api.model.CreateGameRequest
import cz.martinforejt.piskvorky.api.model.GameInvitationsResponse
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.server.features.game.usecase.CancelGameInvitationUseCase
import cz.martinforejt.piskvorky.server.features.game.usecase.JoinGameUseCase
import cz.martinforejt.piskvorky.server.routing.exception.ConflictApiException
import cz.martinforejt.piskvorky.server.routing.utils.currentUser
import cz.martinforejt.piskvorky.server.routing.utils.emptyResponse
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/**
 * Game invitation api routes (protected)
 */
fun Route.gameCreationRoutes() {
    val gameRepository by inject<GameRepository>()
    val joinGameUseCase by inject<JoinGameUseCase>()
    val cancelGameInvitationUseCase by inject<CancelGameInvitationUseCase>()

    route("/game/invite") {

        get("/list") {
            val invitations = gameRepository.getInvitations(currentUser.id)
            call.respond(GameInvitationsResponse(invitations))
        }

        post("/join") {
            val request = call.receive<CreateGameRequest>()
            val res = joinGameUseCase.execute(JoinGameUseCase.Params(currentUser, request))
            if (res.isSuccessful.not()) {
                throw ConflictApiException(res.error?.message ?: "Error occurred.")
            }
            call.emptyResponse()
        }

        post("/cancel") {
            val request = call.receive<CancelGameRequest>()
            val res = cancelGameInvitationUseCase.execute(CancelGameInvitationUseCase.Params(currentUser, request))
            if (res.isSuccessful.not()) {
                throw ConflictApiException(res.error?.message ?: "Error occurred.")
            }
            call.emptyResponse()
        }

    }

}