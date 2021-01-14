package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.domain.model.*

/**
 * Lost password repository
 *
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface LostPasswordRepository {

    /**
     * Store new [link] for [userId]
     */
    suspend fun addLink(userId: Int, link: String)

    /**
     * Get link by [userId] and [link]
     */
    suspend fun getLink(userId: Int, link: String): LostPasswordLink?
}