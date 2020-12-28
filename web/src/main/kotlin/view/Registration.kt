package view

import core.component.CoreComponent
import core.component.CoreRProps
import cz.martinforejt.piskvorky.api.model.RegisterRequest
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.browser.document
import kotlinx.browser.window
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
import react.RState
import react.dom.*
import react.router.dom.redirect
import react.router.dom.routeLink
import react.setState

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class RegisterFormProps : CoreRProps()

class RegisterFormState : RState {
    var email: String = ""
    var password: String = ""
    var passwordConfirm: String = ""
    var error: String? = null
    var signed = false
}

class Registration : CoreComponent<RegisterFormProps, RegisterFormState>() {

    private val authService by inject<AuthenticationService>()

    override fun componentDidMount() {
        document.title = "Piskvorky | Registration"
    }

    override fun RegisterFormState.init() {
        email = ""
        password = ""
        passwordConfirm = ""
        error = null
        signed = false
    }

    override fun RBuilder.render() {
        if (state.signed || hasUser) {
            if (!state.signed) {
                window.alert("Already logged in. Redirecting...")
            }
            redirect(to = "/lobby")
            return
        }
        div("text-center register-root") {
            form(classes = "form-signin panel-box") {
                img(classes = "mg-4", src = "/images/logo2.png", alt = "logo") {}
                h1("h3 mb-3") {
                    +"Registration"
                }
                state.error?.let {
                    div("alert alert-danger") {
                        +it
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
                        name = "email"
                        value = state.email
                        placeholder = "Email address"
                        required = true
                        autoFocus = true
                        onChangeFunction = {
                            setState {
                                email = (it.target as HTMLInputElement).value
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
                button(type = ButtonType.submit, classes = "btn btn-lg btn-block") {
                    attrs {
                        onClickFunction = handleSubmit
                    }
                    +"Register"
                }
                div {
                    +"Already have account? "
                    routeLink("/login") {
                        +"Login"
                    }
                }
                div("mt-5 mb-3 text-mutes") {
                    +"@ 2020 "
                    a(href = "https://martinforejt.cz", target = "_blank") {
                        +"Martin Forejt"
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
                val res = authService.register(RegisterRequest(state.email, state.password))
                if (!res.isSuccessful) {
                    setState {
                        error = when (res.error?.code) {
                            409 -> res.error!!.message
                            else -> "Some error occurred, try it later."
                        }
                    }
                } else {
                    setState {
                        signed = true
                    }
                }
            }
        }
    }
}