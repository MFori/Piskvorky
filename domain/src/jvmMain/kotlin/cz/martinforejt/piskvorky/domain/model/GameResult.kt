package cz.martinforejt.piskvorky.domain.model

import cz.martinforejt.piskvorky.domain.utils.IsoLocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
@Serializable
actual data class GameResult(
    val id: Int? = null,
    val user1: User,
    val user2: User,
    @Serializable(with = IsoLocalDateTimeSerializer::class)
    val created: LocalDateTime,
    val winnerId: Int
)