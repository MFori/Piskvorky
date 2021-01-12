package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import kotlinx.browser.window
import react.*
import react.router.dom.*

abstract class AppProps : CoreRProps()

/**
 * Root app component with router
 */
class AppComponent : CoreComponent<AppProps, CoreRState>() {

    override fun componentDidMount() {
        // if storage is updated from another tab refresh component
        window.addEventListener("storage", {
            setState { }
        }, false)
    }

    override fun RBuilder.render() {
        browserRouter {
            switch {
                route("/", exact = true) {
                    redirect(to = "/lobby")
                }
                privateRoute("/profile") {
                    coreChild(Profile::class)
                }
                privateRoute("/lobby") {
                    coreChild(Lobby::class)
                }
                privateRoute("/game") {
                    coreChild(Game::class)
                }
                route("/login") {
                    coreChild(Login::class)
                }
                route("/register") {
                    coreChild(Registration::class)
                }
                route("/logout") {
                    coreChild(Logout::class)
                }
                route("/lost-password") {
                    coreChild(LostPassword::class)
                }
                route<ResetPasswordProps>("/reset-password/:email/:hash") { rprops ->
                    coreChild(ResetPassword::class) {
                        attrs {
                            email = rprops.match.params.email
                            hash = rprops.match.params.hash
                        }
                    }
                }
                route("") {
                    child(NotFound)
                }
            }
        }
    }

    /**
     * Create private route only for logged users
     * otherwise redirect to login page
     */
    private fun RBuilder.privateRoute(
        path: String,
        exact: Boolean = false,
        strict: Boolean = false,
        render: () -> ReactElement?
    ): ReactElement {
        return route(path, exact, strict) {
            if (hasUser) {
                child<RouteProps<RProps>, RouteComponent<RProps>> {
                    attrs {
                        this.path = path
                        this.exact = exact
                        this.strict = strict
                        this.render = { render() }
                    }
                }
            } else {
                redirect(path, "/login")
            }
        }
    }
}