package cz.martinforejt.piskvorky.server.core.di

import cz.martinforejt.piskvorky.domain.repository.*
import cz.martinforejt.piskvorky.domain.service.EmailService
import cz.martinforejt.piskvorky.server.core.service.EmailServiceImpl
import cz.martinforejt.piskvorky.server.core.service.SocketService
import cz.martinforejt.piskvorky.server.features.admin.usecase.EditUserUseCase
import cz.martinforejt.piskvorky.server.features.game.repository.GameRepositoryImpl
import cz.martinforejt.piskvorky.server.features.game.usecase.CancelGameInvitationUseCase
import cz.martinforejt.piskvorky.server.features.game.usecase.GiveUpGameUseCase
import cz.martinforejt.piskvorky.server.features.game.usecase.JoinGameUseCase
import cz.martinforejt.piskvorky.server.features.game.usecase.PlayMoveUseCase
import cz.martinforejt.piskvorky.server.features.results.repository.ResultsRepositoryImpl
import cz.martinforejt.piskvorky.server.features.results.usecase.AddGameResultUseCase
import cz.martinforejt.piskvorky.server.features.socket.SocketServiceImpl
import cz.martinforejt.piskvorky.server.features.users.manager.HashService
import cz.martinforejt.piskvorky.server.features.users.manager.Sha256HashService
import cz.martinforejt.piskvorky.server.features.users.repository.FriendsRepositoryImpl
import cz.martinforejt.piskvorky.server.features.users.repository.LostPasswordRepositoryImpl
import cz.martinforejt.piskvorky.server.features.users.repository.UsersRepositoryImpl
import cz.martinforejt.piskvorky.server.features.users.usecase.*
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
        UsersRepositoryImpl(
            socketService = get()
        )
    }

    single<FriendsRepository> {
        FriendsRepositoryImpl(
            usersRepository = get(),
            gameRepository = get()
        )
    }

    single<LostPasswordRepository> {
        LostPasswordRepositoryImpl()
    }

    single<GameRepository> {
        GameRepositoryImpl()
    }

    single<ResultsRepository> {
        ResultsRepositoryImpl()
    }

    single<SocketService> {
        SocketServiceImpl(
            gameRepository = get()
        )
    }

    single<HashService> {
        Sha256HashService()
    }

    single<EmailService> {
        EmailServiceImpl()
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

    factory {
        ChangePasswordUseCase(
            usersRepository = get(),
            authenticator = get(),
            hashService = get()
        )
    }

    factory {
        LostPasswordUseCase(
            usersRepository = get(),
            lostPasswordRepository = get(),
            emailService = get()
        )
    }

    factory {
        ResetPasswordUseCase(
            usersRepository = get(),
            lostPasswordRepository = get(),
            hashService = get()
        )
    }

    factory {
        AddFriendUseCase(
            friendsRepository = get(),
            socketService = get()
        )
    }

    factory {
        CancelFriendUseCase(
            friendsRepository = get(),
            socketService = get()
        )
    }

    factory {
        JoinGameUseCase(
            usersRepository = get(),
            gameRepository = get(),
            socketService = get()
        )
    }

    factory {
        CancelGameInvitationUseCase(
            gameRepository = get(),
            socketService = get()
        )
    }

    factory {
        PlayMoveUseCase(
            gameRepository = get(),
            socketService = get(),
            addGameResultUseCase = get()
        )
    }

    factory {
        GiveUpGameUseCase(
            gameRepository = get(),
            socketService = get(),
            usersRepository = get(),
            addGameResultUseCase = get()
        )
    }

    factory {
        AddGameResultUseCase(
            usersRepository = get(),
            resultsRepository = get()
        )
    }

    factory {
        EditUserUseCase(
            usersRepository = get(),
            hashService = get()
        )
    }
}

@KtorExperimentalAPI
private fun Application.property(name: String) = this.environment.config.property(name).getString()