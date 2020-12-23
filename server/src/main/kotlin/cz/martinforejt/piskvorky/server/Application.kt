package cz.martinforejt.piskvorky.server

import cz.martinforejt.piskvorky.domain.core.di.initKoin
import cz.martinforejt.piskvorky.server.core.di.serverModule
import cz.martinforejt.piskvorky.server.routing.registerRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    initKoin(enableNetworkLogs = true, modules = listOf(
        serverModule
    )).koin

    val port = System.getenv("PORT")?.toInt() ?: 9090
    embeddedServer(Netty, port) {
        install(CORS) {
            host("localhost:8080")
            host("localhost:80")
        }

        install(ContentNegotiation) {
            json()
        }

        registerRoutes()
    }.start(wait = true)
}

