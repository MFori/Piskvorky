package view

import appContext
import react.*
import react.dom.*
import kotlinx.coroutines.*
import react.router.dom.*

external interface AppProps : RProps {
    var coroutineScope: CoroutineScope
}

val AppComponent = functionalComponent<AppProps> { props ->
    val context = useContext(appContext)
    val coroutineContext = props.coroutineScope.coroutineContext

    browserRouter {
        switch {
            route("/", exact = true) {
                redirect(to = "lobby")
            }
            route("/lobby") {
                routeLink("/login") {
                    +"Login2"
                }

            }
            route("/game") {
                div { +"game" }
            }
            route("/login") {
                child(Login::class) {
                    attrs {
                        onSubmit = {
                            Application.logger.d { "login submitted" }
                        }
                    }
                }
            }
            route("/register") {
                child(Registration::class) {
                    attrs {
                        onSubmit = {
                            Application.logger.d { "register submitted" }
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