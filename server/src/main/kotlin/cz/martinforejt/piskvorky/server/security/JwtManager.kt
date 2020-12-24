package cz.martinforejt.piskvorky.server.security

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.auth.jwt.*

/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface JwtManager {

    val verifier: JWTVerifier

    val realm: String

    fun generateToken(user: UserPrincipal): String

    fun validateToken(token: String): UserPrincipal?

    fun validateCredential(credential: JWTCredential): UserPrincipal?

}