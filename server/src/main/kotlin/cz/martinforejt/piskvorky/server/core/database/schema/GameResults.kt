package cz.martinforejt.piskvorky.server.core.database.schema

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

/**
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object GameResults : IntIdTable(name = "results") {
    val user1 = reference("user_1", Users.id)
    val user2 = reference("user_2", Users.id)
    val created = datetime("created")
    val winner = integer("winner")
}

class GameResultEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GameResultEntity>(GameResults)

    var user1 by UserEntity referencedOn GameResults.user1
    var user2 by UserEntity referencedOn GameResults.user2
    var created by GameResults.created
    var winner by GameResults.winner
}