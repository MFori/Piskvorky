package cz.martinforejt.piskvorky.domain.model

import java.time.LocalDateTime

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
actual data class LostPasswordLink(
    val userId: Int,
    val link: String,
    val created: LocalDateTime
)