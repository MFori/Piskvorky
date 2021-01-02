package cz.martinforejt.piskvorky.domain.repository

import cz.martinforejt.piskvorky.domain.model.*

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface LostPasswordRepository {

    suspend fun addLink(userId: Int, link: String)

    suspend fun getLink(userId: Int, link: String): LostPasswordLink?
}