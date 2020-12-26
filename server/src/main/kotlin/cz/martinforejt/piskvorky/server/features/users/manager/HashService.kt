package cz.martinforejt.piskvorky.server.features.users.manager

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface HashService {

    fun hashPassword(plain: String): String

}