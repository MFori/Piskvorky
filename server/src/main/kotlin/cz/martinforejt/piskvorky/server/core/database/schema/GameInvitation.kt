package cz.martinforejt.piskvorky.server.core.database.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/**
 * Game invitation table
 */
object GameInvitations : IntIdTable(name = "invitations") {
    val user1 = reference("user_1", Users.id)
    val user2 = reference("user_2", Users.id)
    val created = datetime("created")
    val author = integer("author")

    // compound primary key cannot be used for entity creation without custom id in this exposed version
    //override val primaryKey = PrimaryKey(user1, user2)
}

/**
 * Game invitation entity
 */
class GameInvitationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GameInvitationEntity>(GameInvitations)

    var user1 by UserEntity referencedOn GameInvitations.user1
    var user2 by UserEntity referencedOn GameInvitations.user2
    var created by GameInvitations.created
    var author by GameInvitations.author
}