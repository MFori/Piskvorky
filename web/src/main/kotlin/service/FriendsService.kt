package service

import core.Api
import core.ApiResult
import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.service.FriendsService
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

class FriendsServiceImpl : FriendsService {

    private val format = Json { serializersModule = SerializersModule { } }

    @ExperimentalSerializationApi
    override suspend fun addFriend(request: CreateFriendshipRequest, token: String): Result<Unit> {
        val res = Api.post<Unit>(Api.EP.ADD_FRIEND, format.encodeToString(CreateFriendshipRequest.serializer(), request), token)
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun removeFriend(request: CancelFriendshipRequest, token: String): Result<Unit> {
        val res = Api.post<Unit>(Api.EP.CANCEL_FRIEND, format.encodeToString(CancelFriendshipRequest.serializer(), request), token)
        return if (res.isSuccess) {
            Result(Unit)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun getFriends(token: String): Result<List<ApiUser>> {
        val res = Api.get<UsersListResponse>(Api.EP.FRIENDS_LIST, token)
        return if (res.isSuccess) {
            val data = res.data!!
            Result(data.users)
        } else {
            res.toErrorResult()
        }
    }

    @ExperimentalSerializationApi
    override suspend fun getFriendRequests(token: String): Result<List<FriendRequest>> {
        val res = Api.get<FriendsRequestsResponse>(Api.EP.FRIEND_REQUESTS, token)
        return if (res.isSuccess) {
            val data = res.data!!
            Result(data.requests)
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