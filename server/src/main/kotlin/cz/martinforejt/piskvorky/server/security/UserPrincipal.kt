package cz.martinforejt.piskvorky.server.security

import io.ktor.auth.*

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
data class UserPrincipal(
    val id: Int,
    val email: String
) : Principal