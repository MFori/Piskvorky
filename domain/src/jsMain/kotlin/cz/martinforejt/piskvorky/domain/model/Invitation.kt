package cz.martinforejt.piskvorky.domain.model

/**
 * Created by Martin Forejt on 30.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
actual data class Invitation(
    val userId1: Int,
    val userId2: Int,
    val created: kotlin.js.Date,
    val author: Int
)