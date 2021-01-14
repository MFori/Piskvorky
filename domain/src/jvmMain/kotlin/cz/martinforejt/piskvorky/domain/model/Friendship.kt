package cz.martinforejt.piskvorky.domain.model

import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

actual data class Friendship(
    val userId1: Int,
    val userId2: Int,
    val created: LocalDateTime,
    /** userId of friendship initializer */
    val author: Int,
    /** is friendship pending (= is it request) or is it confirmed friendship? */
    val pending: Boolean
)