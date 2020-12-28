package view

import core.component.CoreComponent
import core.component.CoreRProps
import cz.martinforejt.piskvorky.api.model.LoginRequest
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.browser.document
import kotlinx.browser.localStorage
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
import org.w3c.dom.get
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

class LoginFormProps : CoreRProps()

class LoginFormState : RState {
    var email = ""
    var password = ""
    var error: String? = null
    var signed = false
}

class Login : CoreComponent<LoginFormProps, LoginFormState>() {

    private val authService by inject<AuthenticationService>()

    override fun componentDidMount() {
        document.title = "Piskvorky | Login"
    }

    override fun LoginFormState.init() {
        email = localStorage["last_user"] ?: ""
        password = ""
        error = null
        signed = false
    }

    override fun RBuilder.render() {
        val hasUser = !state.signed && authService.hasUser()
        if (state.signed || hasUser) {
            if(hasUser) {
                window.alert("Already logged in. Redirecting...")
            }
            redirect(to = "/lobby")
            return
        }
        div("text-center login-root") {
            form(classes = "form-signin") {
                img(classes = "mg-4", src = "/images/logo2.png", alt = "logo") {}
                h1("h3 mb-3") {
                    +"Login"
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
                button(type = ButtonType.submit, classes = "btn btn-lg btn-block") {
                    attrs {
                        onClickFunction = handleSubmit
                    }
                    +"Login"
                }
                div {
                    +"Don't have account? "
                    routeLink("/register") {
                        +"Register now"
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
        componentScope.launch {
            val res = authService.login(LoginRequest(state.email, state.password))
            if (!res.isSuccessful) {
                setState {
                    error = when (res.error?.code) {
                        401 -> "Invalid email and/or password."
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