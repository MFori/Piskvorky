package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.api.model.FriendRequest
import cz.martinforejt.piskvorky.domain.model.Friendship
import cz.martinforejt.piskvorky.domain.model.PublicUser

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface FriendsRepository {

    suspend fun deleteFriendship(userId1: Int, userId2: Int)

    suspend fun createFriendship(userId1: Int, userId2: Int)

    suspend fun confirmFriendship(userId1: Int, userId2: Int)

    suspend fun getFriendship(userId1:Int, userId2:Int) : Friendship?

    suspend fun getFriends(userId: Int) : List<PublicUser>

    suspend fun getRequests(userId: Int) : List<FriendRequest>
}