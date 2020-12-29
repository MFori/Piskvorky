package core.component

import co.touchlab.kermit.Kermit
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.koin.core.KoinComponent
import org.koin.core.inject
import react.*
import react.router.dom.redirect
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

interface AppDependencies {
    val logger: Kermit
}

typealias AppContext = RContext<AppDependencies>
//external interface AppContext : RContext<AppDependencies>

class AppContextHolder(val appContext: AppContext)


abstract class CoreRProps : RProps, KoinComponent {
    var context: AppDependencies? = null
    //var location: String = ""
}

abstract class CoreComponent<P : CoreRProps, S : RState> : RComponent<P, S>(), KoinComponent, CoroutineScope {
    final override val coroutineContext: CoroutineContext = Job()
    val componentScope = CoroutineScope(coroutineContext)
    private val authService by inject<AuthenticationService>()
    private var logout = false

    val hasUser
        get() = authService.hasUser()
    val user
        get() = authService.getCurrentUser()

    override fun render() = buildElements {
        if(logout) {
            redirect(to = "/logout")
            return@buildElements
        }
        render()
    }

    fun logout() {
        logout = true
        setState {  }
    }

    fun <P : CoreRProps> RBuilder.coreChild(
        klazz: KClass<out CoreComponent<P, *>>,
        handler: RHandler<P> = {}
    ): ReactElement {
        return klazz.rClass.invoke {
            attrs {
                this.context = props.context
            }
            handler(this)
        }
    }
}

fun <P : CoreRProps> coreFunctionalComponent(
    displayName: String? = null,
    func: RBuilder.(props: P) -> Unit
): FunctionalComponent<P> {
    val fc = { props: P ->
        buildElements {
            func(props)
        }
    }
    if (displayName != null) {
        fc.asDynamic().displayName = displayName
    }
    return fc
}

fun <P : CoreRProps, S : RState> CoreComponent<P, S>.logger() = this.props.logger()

fun CoreRProps.logger() = this.context?.logger

fun <P : CoreRProps, S : RState> CoreComponent<P, S>.rlogger() = this.props.rlogger()

fun CoreRProps.rlogger() = requireNotNull(this.context).logger