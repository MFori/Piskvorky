package cz.martinforejt.piskvorky.server

import cz.martinforejt.piskvorky.domain.core.di.initKoin
import cz.martinforejt.piskvorky.server.core.di.serverModule
import cz.martinforejt.piskvorky.server.routing.registerRoutes
import cz.martinforejt.piskvorky.server.security.setUpSecurity
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    initKoin(enableNetworkLogs = true, modules = listOf(
        serverModule
    )).koin

    val port = System.getenv("PORT")?.toInt() ?: 9090
    //embeddedServer(Netty, port) {
        install(CORS) {
            host("localhost:8080")
            host("localhost:80")
        }

        install(ContentNegotiation) {
            json()
        }

        setUpSecurity()
        registerRoutes()
    //}.start(wait = true)
}

