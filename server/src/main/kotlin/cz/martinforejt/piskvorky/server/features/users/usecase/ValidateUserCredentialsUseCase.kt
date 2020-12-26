package cz.martinforejt.piskvorky.server.features.users.usecase

import cz.martinforejt.piskvorky.domain.usecase.UseCase
import cz.martinforejt.piskvorky.server.security.IUserAuthenticator
import cz.martinforejt.piskvorky.server.security.UserCredential
import cz.martinforejt.piskvorky.server.security.UserPrincipal

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class ValidateUserCredentialsUseCase(
    private val authenticator: IUserAuthenticator
) : UseCase<UserPrincipal?, UserCredential> {

    override fun execute(params: UserCredential): UserPrincipal? {
        return authenticator.authenticate(params)
    }
}