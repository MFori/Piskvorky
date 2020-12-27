package core.di

import core.component.AppContext
import core.component.AppContextHolder
import core.component.AppDependencies
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import org.koin.dsl.module
import react.createContext
import service.AuthenticationServiceImpl

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

    single<AppContextHolder> {
        AppContextHolder(createContext<AppDependencies>())
    }

}