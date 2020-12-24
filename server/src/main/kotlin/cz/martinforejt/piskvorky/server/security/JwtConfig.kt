package cz.martinforejt.piskvorky.server.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import cz.martinforejt.piskvorky.server.core.utils.toUserIDCredential
import io.ktor.auth.jwt.*
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
    override val realm: String,
    private val validityMs: Int,
    private val authenticator: IUserAuthenticator
) : JwtManager {

    private val algorithm = Algorithm.HMAC512(secret)
    override val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    override fun generateToken(user: UserPrincipal): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("id", user.id)
        .withClaim("email", user.email)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    override fun validateToken(token: String): UserPrincipal? {
        val jwt = try {
            verifier.verify(JWT.decode(token))
        } catch (e: JWTDecodeException) {
            return null
        } catch (e2: JWTVerificationException) {
            return null
        }
        return validateCredential(JWTCredential(jwt))
    }

    override fun validateCredential(credential: JWTCredential): UserPrincipal? {
        val userCredential = credential.toUserIDCredential() ?: return null
        return authenticator.authenticate(userCredential)
    }

    private fun getExpiration() = Date(System.currentTimeMillis() + validityMs)

}