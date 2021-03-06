package cz.martinforejt.piskvorky.server.core.database.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/**
 * Users table
 */
object Users : IntIdTable(name = "users") {
    val email = varchar("email", 50)
    val password = text("password")
    val created = datetime("created")
    val admin = bool("admin")
    val active = bool("active")
}

/**
 * User entity
 */
class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password
    var created by Users.created
    var admin by Users.admin
    var active by Users.active

    private val friends1 by FriendshipEntity referrersOn Friendships.user1
    private val friends2 by FriendshipEntity referrersOn Friendships.user2

    /** query list of friends */
    val friends: List<UserEntity>
        get() {
            return friends1.filter { it.pending.not() }.map { it.getFriend(id.value) }
                .toMutableList().apply {
                    addAll(
                        friends2.filter { it.pending.not() }.map { it.getFriend(id.value) }.toList()
                    )
                }.toList()
        }

    /** query list of friend requests */
    val friendRequests: List<UserEntity>
        get() {
            return friends1.filter { it.pending }.map { it.getFriend(id.value) }
                .toMutableList().apply {
                    addAll(
                        friends2.filter { it.pending }.map { it.getFriend(id.value) }.toList()
                    )
                }.toList()
        }
}