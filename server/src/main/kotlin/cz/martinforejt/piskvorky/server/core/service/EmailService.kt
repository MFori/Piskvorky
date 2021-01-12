package cz.martinforejt.piskvorky.server.core.service

import cz.martinforejt.piskvorky.domain.service.EmailService
import org.apache.commons.mail.DefaultAuthenticator

import org.apache.commons.mail.SimpleEmail

import org.apache.commons.mail.Email
import org.apache.commons.mail.EmailException


/**
 * Created by Martin Forejt on 02.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class EmailServiceImpl(private val config: EmailServiceConfig) : EmailService {

    override fun sendEmail(subject: String, message: String, to: Array<String>): Boolean {
        val email: Email = SimpleEmail()
        email.hostName = config.hostName
        email.setSmtpPort(config.port)
        email.setAuthenticator(DefaultAuthenticator(config.userName, config.password))
        email.isSSLOnConnect = config.ssl
        email.setFrom(config.from)
        email.subject = subject
        email.setMsg(message)
        email.addTo(*to)
        return try {
            email.send()
            true
        } catch (e: EmailException) {
            false
        }
    }
}

data class EmailServiceConfig(
    val hostName: String,
    val port: Int,
    val userName: String,
    val password: String,
    val ssl: Boolean,
    val from: String
)