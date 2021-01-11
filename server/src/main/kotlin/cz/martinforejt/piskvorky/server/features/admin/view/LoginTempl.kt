package cz.martinforejt.piskvorky.server.features.admin.view

import io.ktor.http.*
import kotlinx.html.*

/**
 * Created by Martin Forejt on 12.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
fun HTML.loginForm(queryParams: Parameters) {
    body {
        form(method = FormMethod.post) {
            val errorMsg = when {
                "invalid" in queryParams -> "Sorry, incorrect username or password."
                "no" in queryParams -> "Sorry, you need to be logged in to do that."
                else -> null
            }
            if (errorMsg != null) {
                div {
                    style = "color:red;"
                    +errorMsg
                }
            }
            textInput(name = "user") {
                placeholder = "email"
            }
            br
            passwordInput(name = "password") {
                placeholder = "password"
            }
            br
            submitInput {
                value = "Log in"
            }
        }
    }
}