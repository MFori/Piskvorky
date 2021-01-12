package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import cz.martinforejt.piskvorky.api.model.ResetPasswordRequest
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.browser.document
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
import react.router.dom.routeLink
import react.setState

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class ResetPasswordProps : CoreRProps() {
    var email: String = ""
    var hash: String = ""
}

class ResetPasswdFormState : CoreRState() {
    var password: String = ""
    var passwordConfirm: String = ""
    var error: String? = null
    var ok: String? = null
}

/**
 * Reset password component
 */
class ResetPassword : CoreComponent<ResetPasswordProps, ResetPasswdFormState>() {

    private val authService by inject<AuthenticationService>()

    override fun componentDidMount() {
        document.title = "Piskvorky | Reset password"
    }

    override fun ResetPasswdFormState.init() {
        password = ""
        passwordConfirm = ""
        error = null
        ok = null
    }

    override fun RBuilder.render() {
        div("text-center login-root") {
            form(classes = "form-core form-signin panel-box") {
                h1("h3 mt-2 mb-4") {
                    +"Reset password"
                }
                state.error?.let {
                    div("alert alert-danger") {
                        +it
                    }
                }
                state.ok?.let {
                    div("alert alert-success") {
                        +"Password changed. "
                        routeLink("/login") {
                            +"Continue to login"
                        }
                    }
                }
                label("sr-only") {
                    attrs {
                        htmlFor = "email"
                    }
                    +"Email"
                }
                input(type = InputType.email, classes = "form-control") {
                    attrs {
                        id = "email"
                        disabled = true
                        value = props.email
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
                button(type = ButtonType.submit, classes = "btn btn-lg btn-block") {
                    attrs {
                        onClickFunction = handleSubmit
                    }
                    +"Submit"
                }
                div("mb-3") {
                    routeLink("/login") {
                        +"Back to Login"
                    }
                }
            }
        }
    }

    private val handleSubmit: (Event) -> Unit = { event ->
        event.preventDefault()
        if (state.password != state.passwordConfirm) {
            setState { error = "Passwords do not match." }
        } else {
            componentScope.launch {
                val res = authService.resetPassword(ResetPasswordRequest(props.email, props.hash, state.password))
                if (!res.isSuccessful) {
                    setState {
                        error = when (res.error?.code) {
                            409 -> res.error!!.message
                            else -> "Some error occurred, try it later."
                        }
                    }
                } else {
                    setState {
                        ok = "Password changed."
                        error = null
                        password = ""
                        passwordConfirm = ""
                    }
                }
            }
        }
    }

}