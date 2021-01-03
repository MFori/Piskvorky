package cz.martinforejt.piskvorky.domain.model

import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

actual data class Invitation(
    val userId1: Int,
    val userId2: Int,
    val created: LocalDateTime,
    val author: Int
)