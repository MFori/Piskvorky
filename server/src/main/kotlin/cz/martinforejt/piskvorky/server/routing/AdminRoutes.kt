package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.domain.repository.ResultsRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.features.admin.view.HomePageTempl
import cz.martinforejt.piskvorky.server.features.admin.view.adminTemplate
import cz.martinforejt.piskvorky.server.routing.exception.NotFoundApiException
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Route.adminApiRoutes() {

    val usersRepository by inject<UsersRepository>()
    val resultsRepository by inject<ResultsRepository>()

    route("/admin") {

        get("/") {
            call.respond("Hello from admin api.")
        }

        get("/users") {
            val users = usersRepository.getUsers()
            call.respond(mapOf("users" to users))
        }

        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val user =
                id?.let { usersRepository.getUserById(it) } ?: throw NotFoundApiException("User with id not found.")
            call.respond(mapOf("user" to user))
        }

        get("/results") {
            val results = resultsRepository.getResults()
            call.respond(mapOf("results" to results))
        }

    }

}

fun Route.adminWebRoutes() {

    val usersRepository by inject<UsersRepository>()

    route("/admin") {

        static {
            files("src/main/resources/css")
        }

        get("/") {
            val users = usersRepository.getUsers()

            adminTemplate(HomePageTempl(users)) {
                header {

                }
                content {
                    hovnoText {
                        +"Hovno"
                    }
                }
            }
        }


        get("/users") {

        }

        get("/results") {

        }

    }

}