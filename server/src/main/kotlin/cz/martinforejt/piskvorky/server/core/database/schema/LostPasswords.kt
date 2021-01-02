package cz.martinforejt.piskvorky.server.core.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
object LostPasswords : IntIdTable(name = "lost_passwords") {
    val user = reference("user", Users.id)
    val link = varchar("link", 255)
    val created = datetime("created")
}