package cz.martinforejt.piskvorky.domain.service

/**
 * Created by Martin Forejt on 02.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface EmailService {

    fun sendEmail(subject: String, message: String, to: Array<String>)

}
