package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.api.model.Move
import cz.martinforejt.piskvorky.domain.model.toSnap
import cz.martinforejt.piskvorky.domain.repository.GameRepository
import cz.martinforejt.piskvorky.server.features.game.usecase.GiveUpGameUseCase
import cz.martinforejt.piskvorky.server.features.game.usecase.PlayMoveUseCase
import cz.martinforejt.piskvorky.server.routing.exception.ConflictApiException
import cz.martinforejt.piskvorky.server.routing.exception.NotFoundApiException
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
 * Game api routes (protected)
 */
fun Route.gameRoutes() {
    val gameRepository by inject<GameRepository>()
    val playMoveUseCase by inject<PlayMoveUseCase>()
    val giveUpUseCase by inject<GiveUpGameUseCase>()

    route("/game") {

        get("/") {
            val game = gameRepository.getGame(currentUser.id)
            if(game != null) {
                call.respond(game.toSnap())
            } else {
                throw NotFoundApiException("Not in game.")
            }
        }

        post("/play") {
            val request = call.receive<Move>()
            val res = playMoveUseCase.execute(PlayMoveUseCase.Params(currentUser, request))
            if (res.isSuccessful.not()) {
                throw ConflictApiException(res.error?.message ?: "Error occurred.")
            }
            call.emptyResponse()
        }

        post("/giveup") {
            val res = giveUpUseCase.execute(GiveUpGameUseCase.Params(currentUser))
            if (res.isSuccessful.not()) {
                throw ConflictApiException(res.error?.message ?: "Error occurred.")
            }
            call.emptyResponse()
        }

    }

}