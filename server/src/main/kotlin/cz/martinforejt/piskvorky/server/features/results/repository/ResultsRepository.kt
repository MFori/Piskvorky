package cz.martinforejt.piskvorky.server.features.results.repository

import cz.martinforejt.piskvorky.domain.model.GameResult
import cz.martinforejt.piskvorky.domain.repository.ResultsRepository
import cz.martinforejt.piskvorky.server.core.database.schema.GameResultEntity
import cz.martinforejt.piskvorky.server.core.database.schema.GameResults
import cz.martinforejt.piskvorky.server.features.results.mapper.asGameResultDO
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

/**
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class ResultsRepositoryImpl : ResultsRepository {

    override suspend fun getResults(): List<GameResult> = newSuspendedTransaction {
        GameResultEntity.all().map { it.asGameResultDO() }
    }

    override suspend fun getResult(id: Int): GameResult? = newSuspendedTransaction {
        GameResultEntity.findById(id)?.asGameResultDO()
    }

    override suspend fun addResult(result: GameResult): Unit = newSuspendedTransaction {
        GameResults.insert {
            it[user1] = result.user1.id
            it[user2] = result.user2.id
            it[created] = result.created
            it[winner] = result.winnerId
        }
    }

    override suspend fun editResult(result: GameResult): Unit = newSuspendedTransaction {
        GameResults.update({ GameResults.id eq result.id }) {
            it[user1] = result.user1.id
            it[user2] = result.user2.id
            it[created] = result.created
            it[winner] = result.winnerId
        }
    }

    override suspend fun deleteResult(id: Int): Unit = newSuspendedTransaction {
        GameResultEntity.findById(id)?.delete()
    }
}