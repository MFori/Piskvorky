package cz.martinforejt.piskvorky.server.routing

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun Route.adminApiRoutes() {

    route("/admin") {

        get("/") {
            call.respond("Hello from admin")
        }


        get("/users") {

        }

    }

}

fun Route.adminWebRoutes() {

    route("/admin") {

        get("/") {
            call.respond("Hello from admin")
        }


        get("/users") {

        }

    }

}