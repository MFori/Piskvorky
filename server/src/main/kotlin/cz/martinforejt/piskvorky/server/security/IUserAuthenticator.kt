package cz.martinforejt.piskvorky.server.security

/**
 * User authenticator
 *
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
interface IUserAuthenticator {

    /**
     * Authenticate user by [UserCredential]
     *
     * @return [UserPrincipal] or null if [credential] invalid
     */
    fun authenticate(credential: UserCredential): UserPrincipal?

    /**
     * Authenticate user by [UserIdCredential]
     *
     * @return [UserPrincipal] or null if [credential] invalid
     */
    fun authenticate(credential: UserIdCredential): UserPrincipal?

}