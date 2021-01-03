package service

import core.ApiClient
import core.ApiResult
import cz.martinforejt.piskvorky.api.Api
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.service.GameService
import cz.martinforejt.piskvorky.domain.usecase.Error
import cz.martinforejt.piskvorky.domain.usecase.Result
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class GameServiceImpl : GameService {

    private val format = Json { serializersModule = SerializersModule { } }

    @ExperimentalSerializationApi
    override suspend fun createInvitation(request: CreateGameRequest, token: String): Result<Unit> {
        val res = ApiClient.post<Unit>(Api.EP.CREATE_INVITATION, format.encodeToString(CreateGameRequest.serializer(), request), token)
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun cancelInvitation(request: CancelGameRequest, token: String): Result<Unit> {
        val res = ApiClient.post<Unit>(Api.EP.CANCEL_INVITATION, format.encodeToString(CancelGameRequest.serializer(), request), token)
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun getInvitations(token: String): Result<List<GameInvitation>> {
        val res = ApiClient.get<GameInvitationsResponse>(Api.EP.INVITATIONS, token)
        return if (res.isSuccess) {
            val data = res.data!!
            Result(data.invitations)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun play(request: Move, token: String): Result<Unit> {
        val res = ApiClient.post<Unit>(Api.EP.PLAY, format.encodeToString(Move.serializer(), request), token)
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun giveUp(token: String): Result<Unit> {
        val res = ApiClient.post<Unit>(Api.EP.GIVE_UP, null, token)
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun getGame(token: String): Result<GameSnap> {
        val res = ApiClient.get<GameSnap>(Api.EP.GAME, token)
        return if (res.isSuccess) {
            val data = res.data!!
            Result(data)
        } else {
            res.toErrorResult()
        }
    }

    private fun <T : Any> ApiResult<*>.toErrorResult(): Result<T> {
        val error = error!!
        return Result(
            error = Error(
                error.code,
                error.message
            )
        )
    }
}