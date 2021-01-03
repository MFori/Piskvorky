package cz.martinforejt.piskvorky.server.features.users.mapper

import cz.martinforejt.piskvorky.api.model.FriendRequest
import cz.martinforejt.piskvorky.api.utils.ApiUtils.formatApi
import cz.martinforejt.piskvorky.domain.model.Friendship
import cz.martinforejt.piskvorky.server.core.database.schema.FriendshipEntity
import cz.martinforejt.piskvorky.server.core.database.schema.Friendships
import org.jetbrains.exposed.sql.ResultRow

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun ResultRow.asFriendshipDO() = Friendship(
    userId1 = this[Friendships.user1].value,
    userId2 = this[Friendships.user1].value,
    author = this[Friendships.author],
    created = this[Friendships.created],
    pending = this[Friendships.pending]
)

fun FriendshipEntity.toFriendRequest(userId: Int): FriendRequest {
    val friend = if (user1.id.value == userId) user2 else user1
    return FriendRequest(
        userId = friend.id.value,
        email = friend.email,
        created = created.formatApi()
    )
}

