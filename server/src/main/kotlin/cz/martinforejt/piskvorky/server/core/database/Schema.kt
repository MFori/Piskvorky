package cz.martinforejt.piskvorky.server.core.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

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

object Friendships : Table(name = "friendships") {
    val user1 = reference("user_1", Users.id)
    val user2 = reference("user_2", Users.id)
    val created = datetime("created")
    val author = integer("author")
    val pending = bool("pending")

    override val primaryKey = PrimaryKey(user1, user2)
}

/*
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

    override val id: Column<EntityID<FriendshipId>>
        get() = registerColumn("id", FriendshipIdColumnType())
    override val primaryKey = PrimaryKey(user1, user2)
}


class FriendshipEntity(id: EntityID<FriendshipId>) : Entity<FriendshipId>(id) {
    companion object : EntityClass<FriendshipId, FriendshipEntity>(Friendships)

    val user1 by UserEntity referrersOn Friendships.user1
    val user2 by UserEntity referrersOn Friendships.user2
    val created by Friendships.created
    val author by Friendships.author
    val pending by Friendships.pending
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password
    var created by Users.created
    var admin by Users.admin
    var active by Users.active

    val friends by FriendshipEntity via Friendships.user1
    val friends1 by FriendshipEntity referrersOn Friendships.user1
    val friends2 by FriendshipEntity referrersOn Friendships.user2
}*/