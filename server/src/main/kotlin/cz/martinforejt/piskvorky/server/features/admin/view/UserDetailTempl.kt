package cz.martinforejt.piskvorky.server.features.admin.view

import cz.martinforejt.piskvorky.domain.model.User
import kotlinx.html.*

/**
 * Created by Martin Forejt on 11.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class UserDetailTempl(
    private val user: User,
    private var err: String? = null
) : AdminContentTempl {

    override fun FlowContent.apply() {
        h1 {
            +"Users detail: ${user.email}"
        }

        form(method = FormMethod.post) {
            err?.let {
                div("alert alert-danger") {
                    +it
                }
            }

            div("form-group") {
                textInput(classes = "form-control") {
                    disabled = true
                    value = user.email
                }
            }
            div("form-check") {
                checkBoxInput(classes = "form-check-input", name = "admin") {
                    id = "admin"
                    checked = user.admin
                }
                label {
                    htmlFor = "admin"
                    +"Admin"
                }
            }
            div("form-check") {
                checkBoxInput(classes = "form-check-input", name = "active") {
                    id = "active"
                    checked = user.active
                }
                label {
                    htmlFor = "active"
                    +"Active"
                }
            }
            div("form-group") {
                label {
                    htmlFor = "pass"
                    +"Password"
                }
                passwordInput(classes = "form-control", name = "pass") {
                    id = "pass"
                }
                small(classes = "form-text text-muted") {
                    +"Fill only if you want to change password."
                }
            }
            div("form-group") {
                label {
                    htmlFor = "pass-confirm"
                    +"Password confirm"
                }
                passwordInput(classes = "form-control", name = "pass-confirm") {
                    id = "pass-confirm"
                }
                small(classes = "form-text text-muted") {
                    +"Fill only if you want to change password."
                }
            }
            div("row") {
                div("col-6") {
                    submitInput(classes = "btn btn-primary", name = "save") {
                        value = "Save"
                    }
                }
                div("col-6 text-right") {
                    submitInput(classes = "btn btn-danger", name = "delete") {
                        value = "Delete"
                        onClick = "return confirm('Really delete this user?');"
                    }
                }
            }
        }
    }

}