package cz.martinforejt.piskvorky.server

import cz.martinforejt.piskvorky.domain.di.initKoin
import cz.martinforejt.piskvorky.domain.remote.Assignment
import cz.martinforejt.piskvorky.domain.remote.AstroResult
import cz.martinforejt.piskvorky.domain.remote.PeopleInSpaceApi
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.Koin

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val koin = initKoin(enableNetworkLogs = true).koin
    val peopleInSpaceApi = koin.get<PeopleInSpaceApi>()

    val port = System.getenv("PORT")?.toInt() ?: 9090
    embeddedServer(Netty, port) {
        install(CORS) {
            host("localhost:8080")
            host("localhost:80")
        }
        install(ContentNegotiation) {
            json()
        }

        routing {

            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }

            static("/") {
                resources("")
            }

            get("/astros.json") {
                val result = peopleInSpaceApi.fetchPeople()
                call.respond(result)
            }

            get("/astros_local.json") {
                val result = AstroResult("success", 3,
                    listOf(
                        Assignment("ISS", "Chris Cassidy"),
                        Assignment("ISS", "Anatoly Ivanishin"),
                        Assignment("ISS", "Ivan Vagner HOVNO")
                    ))
                call.respond(result)
            }

        }
    }.start(wait = true)

  /*  install(Koin) {
        modules(
            domainModule,
            dataModule,
            serverModule
        )
    }*/

//    Server.start(testing)

    /*val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port) {
        routing {
            get("/hello") {
                call.respondText("Hello, API!")
            }
            get("/") {
                call.respondHtml {
                    head {
                        meta {
                            charset = "utf-8"
                        }
                        title {
                            +"Kotlin full stack application demo"
                        }
                        style {
                            unsafe {
                                //+globalCss.toString()
                            }
                        }
                    }
                    body {
                        img {
                            src = "/ikona.png"
                        }
                        div {
                            id = "piskvorky-app"
                            +"Loading..."
                        }
                        script(src = "/web.js") {
                        }
                    }
                }
                //call.respondText(
                //    this::class.java.classLoader.getResource("index.html")!!.readText(),
                //    ContentType.Text.Html
                //)
            }
            static("/") {
                resources("")
            }
        }
    }.start(wait = true)*/

}

