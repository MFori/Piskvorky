package cz.martinforejt.piskvorky.server.routing

import cz.martinforejt.piskvorky.server.features.admin.LayoutTemplate
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.response.*
import io.ktor.response.respond
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

        get("/results") {

        }

    }

}

fun Route.adminWebRoutes() {

    route("/admin") {

        get("/") {
            call.respondHtmlTemplate(LayoutTemplate()) {
                header {
                    +"Admin"
                }
                content {
                    articleTitle {
                        +"Hello from Admin!"
                    }
                    articleText {
                        +"Admin desc"
                    }
                }
            }
        }


        get("/users") {

        }

    }

}