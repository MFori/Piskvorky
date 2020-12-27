package view

import core.Api
import core.component.CoreComponent
import core.component.CoreRProps
import core.component.rlogger
import cz.martinforejt.piskvorky.api.model.LoginRequest
import cz.martinforejt.piskvorky.api.model.ProfileInfo
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.coroutines.launch
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.koin.core.inject
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RState
import react.dom.*
import react.router.dom.routeLink
import react.setState

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class LoginFormProps : CoreRProps() {
    var onSubmit : ((LoginFormState) -> Unit)? = null
}

class LoginFormState: RState {
    var email: String = ""
    var password: String = ""
    var error: String? = null
}

class Login : CoreComponent<LoginFormProps, LoginFormState>() {

    private val authService by inject<AuthenticationService>()

    override fun LoginFormState.init() {
        email = ""
        password = ""
    }

    override fun RBuilder.render() {
        div {
            form {
                div(classes = "form-group") {
                    label {
                        attrs {
                            htmlFor = "email"
                        }
                        +"Email"
                    }
                    input(classes = "form-control") {
                        attrs {
                            id = "email"
                            name = "email"
                            value = state.email
                            onChangeFunction = {
                                setState {
                                    email = (it.target as HTMLInputElement).value
                                }
                            }
                        }
                    }
                }
                div(classes = "form-group") {
                    label {
                        attrs {
                            htmlFor = "password"
                        }
                        +"Password"
                    }
                    input(classes = "form-control") {
                        attrs {
                            id = "password"
                            name = "password"
                            value = state.password
                            onChangeFunction = {
                                setState {
                                    password = (it.target as HTMLInputElement).value
                                }
                            }
                        }
                    }
                }
                div(classes = "form-group") {
                    button(classes = "form-control") {
                        attrs {
                            onClickFunction = handleSubmit
                        }
                        +"Login"
                    }
                }
            }
            routeLink("/register") {
                +"Register"
            }
        }
    }

    private val handleSubmit: (Event) -> Unit = { event ->
        event.preventDefault()
        componentScope.launch {
            val res = authService.login(LoginRequest(state.email, state.password))
            rlogger().d { "login = $res" }
            if(res.isSuccessful) {
                val res2 = Api.get<ProfileInfo>("/profile", authService.getCurrentUser()!!.token)
                rlogger().d { "profile = $res2" }
            }
        }
        this.props.onSubmit?.invoke(this.state)
    }
}