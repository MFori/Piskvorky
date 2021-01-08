package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import cz.martinforejt.piskvorky.api.model.LostPasswordRequest
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

class LostPasswdFormState : CoreRState() {
    var email = ""
    var error: String? = null
    var ok: String? = null
}

class LostPassword : CoreComponent<CoreRProps, LostPasswdFormState>() {

    private val authService by inject<AuthenticationService>()

    override fun componentDidMount() {
        document.title = "Piskvorky | Lost password"
    }

    override fun LostPasswdFormState.init() {
        email = ""
        error = null
        ok = null
    }

    override fun RBuilder.render() {
        div("text-center login-root") {
            form(classes = "form-core form-signin panel-box") {
                h1("h3 mt-2 mb-4") {
                    +"Lost password"
                }
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
        componentScope.launch {
            val res = authService.lostPassword(LostPasswordRequest(state.email))
            if (!res.isSuccessful) {
                setState {
                    error = when (res.error?.code) {
                        401 -> "Invalid email and/or password."
                        else -> "Some error occurred, try it later."
                    }
                }
            } else {
                setState {
                    ok = "Email with instructions sent."
                    error = null
                    email = ""
                }
            }
        }
    }

}