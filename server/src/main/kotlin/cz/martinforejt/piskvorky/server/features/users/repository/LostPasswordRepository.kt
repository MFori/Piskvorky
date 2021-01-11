package cz.martinforejt.piskvorky.server.features.users.repository

import cz.martinforejt.piskvorky.domain.model.LostPasswordLink
import cz.martinforejt.piskvorky.domain.repository.LostPasswordRepository
import cz.martinforejt.piskvorky.server.core.database.schema.LostPasswords
import cz.martinforejt.piskvorky.server.features.users.mapper.asLostPasswordLink
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class LostPasswordRepositoryImpl : LostPasswordRepository {

    override suspend fun addLink(userId: Int, link: String): Unit = newSuspendedTransaction {
        LostPasswords.insert {
            it[user] = userId
            it[this.link] = link
            it[created] = LocalDateTime.now()
        }
    }

    override suspend fun getLink(userId: Int, link: String): LostPasswordLink? = newSuspendedTransaction {
        LostPasswords.select { (LostPasswords.user eq userId) and (LostPasswords.link eq link) }
            .mapNotNull { it.asLostPasswordLink() }.singleOrNull()
    }
}