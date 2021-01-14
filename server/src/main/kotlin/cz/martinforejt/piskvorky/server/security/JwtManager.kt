package cz.martinforejt.piskvorky.server.security

import com.auth0.jwt.interfaces.JWTVerifier
import io.ktor.auth.jwt.*

/**
 * Jwt manager
 *
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface JwtManager {

    /** jwt verifier */
    val verifier: JWTVerifier

    /** jwt realm */
    val realm: String

    /**
     * Generate token by [UserPrincipal]
     */
    fun generateToken(user: UserPrincipal): String

    /**
     * Validate [token]
     *
     * @return [UserPrincipal] or null if [token] invalid
     */
    fun validateToken(token: String): UserPrincipal?

    /**
     * Validate [credential]
     *
     * @return [UserPrincipal] or null if [credential] invalid
     */
    fun validateCredential(credential: JWTCredential): UserPrincipal?

}