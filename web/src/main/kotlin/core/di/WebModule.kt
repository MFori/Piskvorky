package core.di

import core.component.AppContextHolder
import core.component.AppDependencies
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import cz.martinforejt.piskvorky.domain.service.FriendsService
import org.koin.dsl.module
import react.createContext
import service.AuthenticationServiceImpl
import service.FriendsServiceImpl

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

val webModule = module {

    single<AuthenticationService> {
        AuthenticationServiceImpl()
    }

    single<FriendsService> {
        FriendsServiceImpl()
    }

    single<AppContextHolder> {
        AppContextHolder(createContext<AppDependencies>())
    }

}