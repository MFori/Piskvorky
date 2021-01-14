package cz.martinforejt.piskvorky.domain.service

import cz.martinforejt.piskvorky.api.model.ApiUser
import cz.martinforejt.piskvorky.api.model.CancelFriendshipRequest
import cz.martinforejt.piskvorky.api.model.CreateFriendshipRequest
import cz.martinforejt.piskvorky.api.model.FriendRequest
import cz.martinforejt.piskvorky.domain.usecase.Result

/**
 * Friends service (may be used by client apps)
 *
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