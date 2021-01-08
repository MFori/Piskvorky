package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import cz.martinforejt.piskvorky.api.model.ChangePasswordRequest
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.coroutines.launch
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.koin.core.inject
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.dom.*
import react.setState

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class ChangePasswordState : CoreRState() {
    var passwordCurrent: String = ""
    var password: String = ""
    var passwordConfirm: String = ""
    var error: String? = null
    var ok: String? = null
}

class ChangePassword : CoreComponent<CoreRProps, ChangePasswordState>() {

    private val authService by inject<AuthenticationService>()

    override fun ChangePasswordState.init() {
        passwordCurrent = ""
        password = ""
        passwordConfirm = ""
        error = null
        ok = null
    }

    override fun RBuilder.render() {
        form(classes = "form-core mt-3") {
            state.error?.let {
                div("alert alert-danger") {
                    +it
                }
            }
            state.ok?.let {
                div("alert alert-success") {
                    +it
                }
            }
            label("sr-only") {
                attrs {
                    htmlFor = "password-current"
                }
                +"Current password"
            }
            input(type = InputType.password, classes = "form-control") {
                attrs {
                    id = "password-current"
                    name = "password-current"
                    value = state.passwordCurrent
                    placeholder = "Current Password"
                    required = true
                    onChangeFunction = {
                        setState {
                            passwordCurrent = (it.target as HTMLInputElement).value
                        }
                    }
                }
            }
            label("sr-only") {
                attrs {
                    htmlFor = "password"
                }
                +"Password"
            }
            input(type = InputType.password, classes = "form-control") {
                attrs {
                    id = "password"
                    name = "password"
                    value = state.password
                    placeholder = "Password"
                    required = true
                    onChangeFunction = {
                        setState {
                            password = (it.target as HTMLInputElement).value
                        }
                    }
                }
            }
            label("sr-only") {
                attrs {
                    htmlFor = "password-confirm"
                }
                +"Password confirm"
            }
            input(type = InputType.password, classes = "form-control") {
                attrs {
                    id = "password-confirm"
                    name = "password-confirm"
                    value = state.passwordConfirm
                    placeholder = "Password confirm"
                    required = true
                    onChangeFunction = {
                        setState {
                            passwordConfirm = (it.target as HTMLInputElement).value
                        }
                    }
                }
            }
            button(type = ButtonType.submit, classes = "btn") {
                attrs {
                    onClickFunction = handleSubmit
                }
                +"Change password"
            }
        }
    }

    private val handleSubmit: (Event) -> Unit = { event ->
        event.preventDefault()
        if (state.password != state.passwordConfirm) {
            setState { error = "Passwords do not match." }
        } else {
            componentScope.launch {
                val res = authService.changePassword(
                    ChangePasswordRequest(
                        user!!.email,
                        state.password,
                        state.passwordCurrent
                    ), user!!.token
                )
                if (!res.isSuccessful) {
                    setState {
                        error = when (res.error?.code) {
                            401 -> "Invalid current password."
                            409 -> res.error!!.message
                            else -> "Some error occurred, try it later."
                        }
                        ok = null
                    }
                } else {
                    setState {
                        ok = "Password successfully changed"
                        error = null
                        password = ""
                        passwordConfirm = ""
                        passwordCurrent = ""
                    }
                }
            }
        }
    }

}