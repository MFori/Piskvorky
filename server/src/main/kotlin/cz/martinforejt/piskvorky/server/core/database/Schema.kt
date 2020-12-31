package cz.martinforejt.piskvorky.server.core.database

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.vendors.currentDialect

/**
 * Created by Martin Forejt on 25.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

object Users : IntIdTable(name = "users") {
    val email = varchar("email", 50)
    val password = text("password")
    val created = datetime("created")
    val admin = bool("admin")
    val active = bool("active")
}

class FriendshipId(
    val id1: Int,
    val id2: Int
) : Comparable<FriendshipId> {
    override fun compareTo(other: FriendshipId): Int {
        return if (id1 != other.id1) {
            id1.compareTo(other.id1)
        } else if (id2 != other.id2) {
            id2.compareTo(other.id2)
        } else {
            id1.compareTo(other.id2)
        }
    }
}

class FriendshipIdColumnType : ColumnType() {
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
        return super.valueToDB(value)
    }
}


object Friendships : IdTable<FriendshipId>(name = "friendships") {
    val user1 = reference("user_1", Users.id)
    val user2 = reference("user_2", Users.id)
    val created = datetime("created")
    val author = integer("author")
    val pending = bool("pending")

    override val id = registerColumn<FriendshipId>("id", FriendshipIdColumnType()).entityId()
    override val primaryKey = PrimaryKey(user1, user2)
}


class FriendshipEntity(id: EntityID<FriendshipId>) : Entity<FriendshipId>(id) {
    companion object : EntityClass<FriendshipId, FriendshipEntity>(Friendships)

    var user1 by UserEntity referencedOn Friendships.user1
    var user2 by UserEntity referencedOn Friendships.user2
    var created by Friendships.created
    var author by Friendships.author
    var pending by Friendships.pending
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password
    var created by Users.created
    var admin by Users.admin
    var active by Users.active

    private val friends1 by FriendshipEntity referrersOn Friendships.user1
    private val friends2 by FriendshipEntity referrersOn Friendships.user2

    val friends: List<UserEntity>
        get() {
              return friends1.map { it.getFriend(id.value) }
                .toMutableList().apply {
                    addAll(
                        friends2.map { it.getFriend(id.value) }.toList()
                    )
                }.toList()
        }
}

private fun FriendshipEntity.getFriend(userId: Int): UserEntity =
    if (user1.id.value == userId) {
        user2
    } else {
        user1
    }