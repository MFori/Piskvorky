package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.rlogger
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.router.dom.routeLink

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class RegisterFormProps : CoreRProps() {
    var onSubmit : ((RegisterFormState) -> Unit)? = null
}

class RegisterFormState: RState {
    var email: String = ""
    var password: String = ""
    var passwordConfirm: String = ""
    var error: String? = null
}

class Registration : CoreComponent<RegisterFormProps, RegisterFormState>() {

    override fun RegisterFormState.init() {
        email = ""
        password = ""
        passwordConfirm = ""
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
                    label {
                        attrs {
                            htmlFor = "password-confirm"
                        }
                        +"Password Confirm"
                    }
                    input(classes = "form-control") {
                        attrs {
                            id = "password-confirm"
                            name = "password-confirm"
                            value = state.passwordConfirm
                            onChangeFunction = {
                                setState {
                                    passwordConfirm = (it.target as HTMLInputElement).value
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
                        +"Register"
                    }
                }
            }
            routeLink("/login") {
                +"Login"
            }
        }
    }

    private val handleSubmit: (Event) -> Unit = { event ->
        rlogger().d { "email = ${this.state.email}" }
        rlogger().d { "password = ${this.state.password}" }
        rlogger().d { "passwordConfirm = ${this.state.passwordConfirm}" }
        event.preventDefault()
        this.props.onSubmit?.invoke(this.state)
    }
}