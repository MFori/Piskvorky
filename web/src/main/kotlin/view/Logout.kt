package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.browser.localStorage
import org.koin.core.inject
import org.w3c.dom.set
import react.RBuilder
import react.router.dom.redirect

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class Logout : CoreComponent<CoreRProps, CoreRState>() {

    private val authService by inject<AuthenticationService>()

    override fun componentDidMount() {
        user?.let {
            localStorage["last_user"] = it.email
        }
        authService.logout()
    }

    override fun RBuilder.render() {
        redirect("/logout", "/login")
    }

}