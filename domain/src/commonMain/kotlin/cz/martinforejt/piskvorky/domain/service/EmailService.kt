package cz.martinforejt.piskvorky.domain.service

/**
 * Email service
 *
 * Created by Martin Forejt on 02.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface EmailService {

    /**
     * Send email
     *
     * @param subject
     * @param message
     * @param to list of recipients emails
     * @return success?
     */
    fun sendEmail(subject: String, message: String, to: Array<String>): Boolean

}
