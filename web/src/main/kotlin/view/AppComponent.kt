package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.rlogger
import react.RBuilder
import react.RState
import react.child
import react.dom.div
import react.router.dom.*

abstract class AppProps : CoreRProps()

class AppComponent : CoreComponent<AppProps, RState>() {

    override fun RBuilder.render() {
        browserRouter {
            switch {
                route("/", exact = true) {
                    redirect(to = "lobby")
                }
                route("/lobby") {
                    routeLink("/login") {
                        +"Login"
                    }

                }
                route("/game") {
                    div { +"game" }
                }
                route("/login") {
                    coreChild(Login::class) {
                        attrs {
                            onSubmit = {
                                props.rlogger().d { "login submitted" }
                            }
                        }
                    }
                }
                route("/register") {
                    coreChild(Registration::class) {
                        attrs {
                            onSubmit = {
                                props.rlogger().d { "register submitted" }
                            }
                        }
                    }
                }
                route("") {
                    child(NotFound)
                }
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