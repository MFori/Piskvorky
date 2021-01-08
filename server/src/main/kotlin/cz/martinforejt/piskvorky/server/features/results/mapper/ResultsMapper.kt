package cz.martinforejt.piskvorky.server.features.results.mapper

import cz.martinforejt.piskvorky.domain.model.GameResult
import cz.martinforejt.piskvorky.server.core.database.schema.GameResultEntity
import cz.martinforejt.piskvorky.server.features.users.mapper.asUserDO

/**
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
fun GameResultEntity.asGameResultDO() = GameResult(
    id = this.id.value,
    user1 = this.user1.asUserDO(),
    user2 = this.user2.asUserDO(),
    created = this.created,
    winnerId = this.winner
)