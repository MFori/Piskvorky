package cz.martinforejt.piskvorky.domain.model

import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
actual data class GameResult(
    val id: Int? = null,
    val user1: User,
    val user2: User,
    val created: LocalDateTime,
    val winnerId: Int
)