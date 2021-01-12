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
    head {
        title("Piskvorky | Admin")
        link("/admin/bootstrap.min.css", rel = "stylesheet")
        link("/admin/admin.css", rel = "stylesheet")
    }
    body {
        form(method = FormMethod.post) {
            id = "login-form"
            h1 {
                +"Admin zone - login"
            }
            val errorMsg = when {
                "invalid" in queryParams -> "Invalid email and/or password."
                "no" in queryParams -> "Sorry, you need to be logged in to do that."
                else -> null
            }
            if (errorMsg != null) {
                div("alert alert-danger") {
                    +errorMsg
                }
            }
            div("form-group") {
                textInput(classes= "form-control", name = "user") {
                    placeholder = "email"
                }
            }
            div("form-group") {
                passwordInput(classes= "form-control", name = "password") {
                    placeholder = "password"
                }
            }
            submitInput(classes="btn btn-primary btn-block") {
                value = "Log in"
            }
        }
    }
}