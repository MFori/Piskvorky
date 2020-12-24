package cz.martinforejt.piskvorky.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class JwtConfig(
    private val issuer: String,
    secret: String,
    private val validityMs: Int,
) {

    private val algorithm = Algorithm.HMAC512(secret)
    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun generateToken(user: UserPrincipal) = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id)
        .withClaim("email", user.email)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityMs)

}