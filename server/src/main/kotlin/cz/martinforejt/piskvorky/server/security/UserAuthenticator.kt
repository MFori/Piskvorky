package cz.martinforejt.piskvorky.server.security


/**
 * Created by Martin Forejt on 24.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class UserAuthenticator : IUserAuthenticator {

    override fun authenticate(credential: UserCredential): UserPrincipal? {
        if (credential.email == "admin@admin.com" && credential.password == "pass123") {
            return UserPrincipal(1, credential.email)
        }
        return null
    }

    override fun authenticate(credential: UserIdCredential): UserPrincipal? {
        return UserPrincipal(credential.id, credential.email)
    }
}