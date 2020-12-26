package cz.martinforejt.piskvorky.server.core.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.datetime

/**
 * Created by Martin Forejt on 25.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

object Users: IntIdTable(name = "users") {
    val email = varchar("email", 50)
    val password = text("password")
    val created = datetime("created")
    val admin = bool("admin")
    val active = bool("active")
}