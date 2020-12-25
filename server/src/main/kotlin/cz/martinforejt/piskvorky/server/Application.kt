package cz.martinforejt.piskvorky.server

import cz.martinforejt.piskvorky.domain.core.di.initKoin
import cz.martinforejt.piskvorky.server.core.di.serverModule
import cz.martinforejt.piskvorky.server.routing.registerRoutes
import cz.martinforejt.piskvorky.server.security.setUpSecurity
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.websocket.*
import java.time.Duration

fun main(args: Array<String>): Unit = EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging)

    initKoin(
        enableNetworkLogs = true, modules = listOf(
            serverModule(this)
        )
    ).koin

    install(CORS) {
        host("localhost:8080")
        host("localhost:80")
    }

    install(ContentNegotiation) {
        json()
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    setUpSecurity()
    registerRoutes()
}

