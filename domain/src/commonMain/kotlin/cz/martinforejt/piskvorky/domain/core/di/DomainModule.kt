package cz.martinforejt.piskvorky.domain.core.di

import co.touchlab.kermit.Kermit
import cz.martinforejt.piskvorky.domain.remote.PeopleInSpaceApi
import cz.martinforejt.piskvorky.domain.repository.PeopleInSpaceRepository
import cz.martinforejt.piskvorky.domain.repository.getLogger
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun domainModule(enableNetworkLogs: Boolean) = module {
    single { createJson() }
    single { createHttpClient(get(), enableNetworkLogs = enableNetworkLogs) }
    single { PeopleInSpaceRepository() }
    single { PeopleInSpaceApi(get()) }
    single { Kermit(getLogger()) }
}

fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true }

fun createHttpClient(json: Json, enableNetworkLogs: Boolean) = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(json)
    }
    if (enableNetworkLogs) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
