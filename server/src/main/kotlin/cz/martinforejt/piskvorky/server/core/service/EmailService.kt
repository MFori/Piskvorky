package cz.martinforejt.piskvorky.server.core.service

import cz.martinforejt.piskvorky.domain.service.EmailService
import org.apache.commons.mail.DefaultAuthenticator

import org.apache.commons.mail.SimpleEmail

import org.apache.commons.mail.Email


/**
 * Created by Martin Forejt on 02.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class EmailServiceImpl : EmailService {

    override fun sendEmail(subject: String, message: String, to: Array<String>) {
        // TODO read production configuration form conf file
        val email: Email = SimpleEmail()
        email.hostName = "smtp.googlemail.com"
        email.setSmtpPort(465)
        email.setAuthenticator(DefaultAuthenticator("username", "password"))
        email.isSSLOnConnect = true
        email.setFrom("user@gmail.com")
        email.subject = subject
        email.setMsg(message)
        email.addTo(*to)
        email.send()
    }
}
