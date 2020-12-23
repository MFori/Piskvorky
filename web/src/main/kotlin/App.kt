import cz.martinforejt.piskvorky.api.model.UserResponse
import cz.martinforejt.piskvorky.domain.remote.Assignment
import react.*
import react.dom.*
import kotlinx.coroutines.*
import react.router.dom.routeLink


val App = functionalComponent<RProps> { _ ->
    val appDependencies = useContext(AppDependenciesContext)
    val repository = appDependencies.repository

    val (people, setPeople) = useState(emptyList<Assignment>())

    useEffectWithCleanup(dependencies = listOf()) {
        val mainScope = MainScope()

        mainScope.launch {
            setPeople(repository.fetchPeople())
        }
        return@useEffectWithCleanup { mainScope.cancel() }
    }

    h1 {
        +"Piskvorky"
    }
    ul {
        people.forEach { item ->
            li {
                +"${item.name} (${item.craft})"
            }
        }
    }
    routeLink("/login") {
        +"Login2"
    }
}
