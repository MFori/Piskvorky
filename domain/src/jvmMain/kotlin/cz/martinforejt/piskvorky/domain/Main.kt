package cz.martinforejt.piskvorky.domain

import cz.martinforejt.piskvorky.domain.di.initKoin
import cz.martinforejt.piskvorky.domain.remote.PeopleInSpaceApi
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val koin = initKoin(enableNetworkLogs = true).koin
        val api = koin.get<PeopleInSpaceApi>()
        println(api.fetchPeople())
    }
}
