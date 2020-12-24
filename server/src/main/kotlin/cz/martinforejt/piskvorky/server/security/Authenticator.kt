package cz.martinforejt.piskvorky.server.security

/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

interface IUserAuthenticator {

    fun authenticate(credential: UserCredential): UserPrincipal?

    fun authenticate(credential: UserIdCredential): UserPrincipal?

}