package cz.martinforejt.piskvorky.server.core.di

import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.features.users.manager.HashService
import cz.martinforejt.piskvorky.server.features.users.manager.Sha256HashService
import cz.martinforejt.piskvorky.server.features.users.repository.UsersRepositoryImpl
import cz.martinforejt.piskvorky.server.features.users.usecase.RegisterUserUseCase
import cz.martinforejt.piskvorky.server.features.users.usecase.ValidateUserCredentialsUseCase
import cz.martinforejt.piskvorky.server.security.IUserAuthenticator
import cz.martinforejt.piskvorky.server.security.JwtConfig
import cz.martinforejt.piskvorky.server.security.JwtManager
import cz.martinforejt.piskvorky.server.security.UserAuthenticator
import io.ktor.application.*
import io.ktor.util.*
import org.koin.dsl.module

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

@KtorExperimentalAPI
fun serverModule(app: Application) = module {

    single<JwtManager> {
        val jwtIssuer = app.property("jwt.domain")
        val jwtSecret = app.property("jwt.secret")
        val jwtRealm = app.property("jwt.realm")
        val jwtValidity = app.property("jwt.validity_ms").toInt()

        JwtConfig(jwtIssuer, jwtSecret, jwtRealm, jwtValidity, authenticator = get())
    }

    single<IUserAuthenticator> {
        UserAuthenticator(
            usersRepository = get(),
            hashService = get()
        )
    }

    single<UsersRepository> {
        UsersRepositoryImpl()
    }

    single<HashService> {
        Sha256HashService()
    }

    factory {
        ValidateUserCredentialsUseCase(
            authenticator = get()
        )
    }

    factory {
        RegisterUserUseCase(
            usersRepository = get(),
            hashService = get()
        )
    }
}

@KtorExperimentalAPI
private fun Application.property(name: String) = this.environment.config.property(name).getString()