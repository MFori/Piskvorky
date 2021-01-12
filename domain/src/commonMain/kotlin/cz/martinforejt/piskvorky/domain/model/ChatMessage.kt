package cz.martinforejt.piskvorky.domain.model

import kotlinx.serialization.Serializable

/**
 * Created by Martin Forejt on 12.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
@Serializable
data class ChatMessage(
    val from: String,
    val message: String,
    val date: Long
)