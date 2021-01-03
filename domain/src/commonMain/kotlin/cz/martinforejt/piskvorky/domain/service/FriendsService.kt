package cz.martinforejt.piskvorky.domain.service

import cz.martinforejt.piskvorky.api.model.*
import cz.martinforejt.piskvorky.domain.model.UserWithToken
import cz.martinforejt.piskvorky.domain.usecase.Result

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

interface FriendsService {

    suspend fun addFriend(request: CreateFriendshipRequest, token: String): Result<Unit>

    suspend fun removeFriend(request: CancelFriendshipRequest, token: String): Result<Unit>

    suspend fun getFriends(token: String): Result<List<ApiUser>>

    suspend fun getFriendRequests(token: String): Result<List<FriendRequest>>

}