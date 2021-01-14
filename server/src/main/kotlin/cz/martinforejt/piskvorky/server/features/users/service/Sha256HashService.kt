package cz.martinforejt.piskvorky.server.features.users.service

import cz.martinforejt.piskvorky.domain.service.HashService
import io.ktor.util.*
import java.util.*

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
@KtorExperimentalAPI
class Sha256HashService(private val salt: String = "p4sfds2") : HashService {

    private val digestFunction = getDigestFunction("SHA-256") { password ->
        "$salt${password.length}"
    }

    override fun hashPassword(plain: String): String {
        return String(
            Base64.getEncoder().encode(
                digestFunction(plain)
            )
        )
    }
}