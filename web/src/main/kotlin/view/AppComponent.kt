package view

import core.component.CoreComponent
import core.component.CoreRProps
import kotlinx.browser.window
import react.*
import react.dom.em
import react.router.dom.*

abstract class AppProps : CoreRProps()

class AppComponent : CoreComponent<AppProps, RState>() {

    override fun componentDidMount() {
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
                    routeLink("/logout") {
                        +"Logout"
                    }
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
                route<ResetPasswordProps>("/reset-password/:email/:hash") { rprops->
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

    private fun RBuilder.privateRoute(
        path: String,
        exact: Boolean = false,
        strict: Boolean = false,
        render: () -> ReactElement?
    ): ReactElement {
        return route(path, exact, strict) {
            if(hasUser) {
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


/*
val App = functionalComponent<RProps> { _ ->
    val appDependencies = useContext(AppDependenciesContext)
    //val repository = appDependencies.repository

    val (people, setPeople) = useState(emptyList<Assignment>())

    useEffectWithCleanup(dependencies = listOf()) {
        val mainScope = MainScope()

        mainScope.launch {
            // setPeople(repository.fetchPeople())
        }

        return@useEffectWithCleanup { mainScope.cancel() }
    }

}*/