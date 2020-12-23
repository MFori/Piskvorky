package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.domain.remote.Assignment
import cz.martinforejt.piskvorky.domain.remote.AstroResult
import cz.martinforejt.piskvorky.domain.remote.PeopleInSpaceApi
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 23.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
fun Route.exampleRoutes() {

    val peopleInSpaceApi: PeopleInSpaceApi by inject()

    get("/") {
        call.respondText(
            "<h1>Hello world</h1>",
            ContentType.Text.Html
        )
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