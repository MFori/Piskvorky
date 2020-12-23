import co.touchlab.kermit.Kermit
import cz.martinforejt.piskvorky.domain.core.di.initKoin
import cz.martinforejt.piskvorky.domain.repository.PeopleInSpaceRepository
import org.koin.core.KoinComponent
import org.koin.core.get
import react.child
import react.createContext
import react.dom.a
import react.dom.render
import react.router.dom.*

object AppDependencies : KoinComponent {
    val repository: PeopleInSpaceRepository
    val logger: Kermit

    init {
        initKoin()
        repository = get()
        logger = get()
    }
}

val AppDependenciesContext = createContext<AppDependencies>()


fun main() {
    render(kotlinx.browser.document.getElementById("root")) {
        AppDependenciesContext.Provider(AppDependencies) {
            browserRouter {
                switch {
                    route("/", exact = true) {
                        child(App)
                    }
                    route("/ha") {
                        routeLink("/login") {
                            +"Login"
                        }
                        a(href = "#/") {
                            +"Back"
                        }
                        routeLink("/") {
                            +"Root"
                        }
                    }
                    route("/login") {
                        routeLink("/") {
                            +"Back"
                        }
                    }
                }
            }
        }
    }
}