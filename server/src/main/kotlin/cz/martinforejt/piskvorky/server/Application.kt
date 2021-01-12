package cz.martinforejt.piskvorky.server

import cz.martinforejt.piskvorky.domain.core.di.initKoin
import cz.martinforejt.piskvorky.server.core.di.serverModule
import cz.martinforejt.piskvorky.server.core.database.DatabaseFactory
import cz.martinforejt.piskvorky.server.routing.SocketCookieSession
import cz.martinforejt.piskvorky.server.routing.registerRoutes
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import cz.martinforejt.piskvorky.server.security.setUpSecurity
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.serialization.*
import io.ktor.server.netty.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*
import org.slf4j.event.Level
import java.time.Duration

fun main(args: Array<String>): Unit = EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
    }

    initKoin(
        modules = listOf(
            serverModule(this)
        )
    ).koin

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()

        //host("localhost:8080")
        //host("localhost:80")
    }

    install(ContentNegotiation) {
        json()
    }

    install(Sessions) {
        cookie<SocketCookieSession>("LOBBY_SESSION")
        cookie<UserPrincipal>(
            "admin-auth",
            storage = SessionStorageMemory()
        ) {
            cookie.path = "/"
        }
    }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(10)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    DatabaseFactory.init()

    setUpSecurity()
    registerRoutes()
}

