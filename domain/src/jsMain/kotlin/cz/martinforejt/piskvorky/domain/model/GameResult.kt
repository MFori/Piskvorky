package cz.martinforejt.piskvorky.domain.model

/**
 * Created by Martin Forejt on 08.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
actual data class GameResult(
    val id: Int,
    val user1: User,
    val user2: User,
    val created: kotlin.js.Date,
    /** userId of winner */
    val winnerId: Int
)