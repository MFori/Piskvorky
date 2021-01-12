package cz.martinforejt.piskvorky.domain.core.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    modules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
) =
    startKoin {
        appDeclaration()

        modules(
            modules.toMutableList().apply {
                add(
                    domainModule()
                )
            }
        )
    }

// called by iOS,Android,js etc
fun initKoin(modules: List<Module> = emptyList()) = initKoin(modules = modules) {}