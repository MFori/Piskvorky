package cz.martinforejt.piskvorky.domain.service

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface HashService {

    fun hashPassword(plain: String): String

}