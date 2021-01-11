package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.domain.repository.ResultsRepository
import cz.martinforejt.piskvorky.domain.repository.UsersRepository
import cz.martinforejt.piskvorky.server.features.admin.view.*
import cz.martinforejt.piskvorky.server.routing.exception.NotFoundApiException
import cz.martinforejt.piskvorky.server.security.FORM_AUTH_ADMIN
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.html.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
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
    val resultsRepository by inject<ResultsRepository>()

    route("/admin") {

        static {
            files("src/main/resources/css")
        }

        get("/") {
            adminTemplate<HomePageTempl> {}
        }

        get("/users") {
            val users = usersRepository.getUsers()
            adminTemplate(UsersListTempl(users)) { }
        }

        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val user =
                id?.let { usersRepository.getUserById(it) } ?: throw NotFoundApiException("User with id not found.")
            call.respond(mapOf("user" to user))
        }

        get("/results") {
            val results = resultsRepository.getResults()
            adminTemplate(ResultsListTempl(results)) { }
        }
    }

}

fun Route.adminPublicWebRoutes() {
    route("/login") {

        get {
            call.respondHtml {
                loginForm(call.request.queryParameters)
            }
        }

        authenticate(FORM_AUTH_ADMIN) {
            post {
                val principal = call.principal<UserPrincipal>()!!
                call.sessions.set(principal)
                call.respondRedirect("/admin")
            }
        }

    }

    get("/logout") {
        call.sessions.clear<UserPrincipal>()
        call.respondRedirect("/login")
    }
}