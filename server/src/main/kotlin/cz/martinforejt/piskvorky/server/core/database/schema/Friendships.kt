package cz.martinforejt.piskvorky.server.core.database.schema

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.VarCharColumnType
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.io.Serializable

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object Friendships : IdTable<FriendshipId>(name = "friendships") {
    val user1 = reference("user_1", Users.id)
    val user2 = reference("user_2", Users.id)
    val created = datetime("created")
    val author = integer("author")
    val pending = bool("pending")

    override val id = registerColumn<FriendshipId>("id", FriendshipIdColumnType()).entityId()
    override val primaryKey = PrimaryKey(user1, user2)
}

fun FriendshipEntity.getFriend(userId: Int): UserEntity =
    if (user1.id.value == userId) {
        user2
    } else {
        user1
    }

class FriendshipId(
    val id1: Int,
    val id2: Int
) : Comparable<FriendshipId>, Serializable {
    override fun compareTo(other: FriendshipId): Int {
        return when {
            id1 != other.id1 -> {
                id1.compareTo(other.id1)
            }
            id2 != other.id2 -> {
                id2.compareTo(other.id2)
            }
            else -> {
                id1.compareTo(other.id2)
            }
        }
    }
}

class FriendshipIdColumnType : VarCharColumnType(15) {
    override fun sqlType(): String = currentDialect.dataTypeProvider.textType()
    override fun valueFromDB(value: Any): FriendshipId = when (value) {
        is String -> {
            val parts = value.split(":")
            FriendshipId(parts[0].toInt(), parts[1].toInt())
        }
        else -> error("Unexpected value of type Int: $value of ${value::class.qualifiedName}")
    }

    override fun valueToDB(value: Any?): Any? {
        if (value is FriendshipId) {
            return "${value.id1}:${value.id2}"
        }
        throw IllegalArgumentException()
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is FriendshipId) {
            return "${value.id1}:${value.id2}"
        }
        throw IllegalArgumentException()
    }

    override fun valueToString(value: Any?): String {
        if (value is FriendshipId) {
            return "${value.id1}:${value.id2}"
        }
        throw IllegalArgumentException()
    }

    override fun nonNullValueToString(value: Any): String {
        if (value is FriendshipId) {
            return "${value.id1}:${value.id2}"
        }
        throw IllegalArgumentException()
    }
}

class FriendshipEntity(id: EntityID<FriendshipId>) : Entity<FriendshipId>(id) {
    companion object : EntityClass<FriendshipId, FriendshipEntity>(Friendships)

    var user1 by UserEntity referencedOn Friendships.user1
    var user2 by UserEntity referencedOn Friendships.user2
    var created by Friendships.created
    var author by Friendships.author
    var pending by Friendships.pending

}
