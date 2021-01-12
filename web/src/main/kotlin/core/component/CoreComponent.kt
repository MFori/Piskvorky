package core.component

import co.touchlab.kermit.Kermit
import cz.martinforejt.piskvorky.domain.service.AuthenticationService
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.html.role
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.span
import react.router.dom.redirect
import service.WebSocketService
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
    val socketService: WebSocketService
}

typealias AppContext = RContext<AppDependencies>

class AppContextHolder(val appContext: AppContext)


abstract class CoreRProps : RProps, KoinComponent {
    var context: AppDependencies? = null
}

abstract class CoreRState : RState {
    var dialogs: MutableList<DialogBuilder>? = null
}

/**
 * Core react component, every app component may extend this class
 *
 * @param P props
 * @param S state
 */
abstract class CoreComponent<P : CoreRProps, S : CoreRState> : RComponent<P, S>(), KoinComponent, CoroutineScope {
    final override val coroutineContext: CoroutineContext = Job()
    val componentScope = CoroutineScope(coroutineContext)
    private val authService by inject<AuthenticationService>()
    var mayLogout = false
    val hasUser
        get() = authService.hasUser()
    val user
        get() = authService.getCurrentUser()

    open val customLogout = false

    override fun render() = buildElements {
        if (mayLogout && !customLogout) {
            redirect(to = "/logout")
            mayLogout = false
            return@buildElements
        }
        render()
        state.dialogs?.forEachIndexed { index, dialogBuilder ->
            dialogBuilder.dismissCallback {
                setState { dialogs?.removeAt(index) }
            }.build(this)
        }
    }

    fun logout() {
        if (mayLogout) return
        mayLogout = true
        setState { }
    }

    fun <P : CoreRProps> RBuilder.coreChild(
        klazz: KClass<out CoreComponent<P, *>>,
        handler: RHandler<P> = {}
    ): ReactElement {
        return coreChild(this@CoreComponent, klazz, handler)
    }

    fun RBuilder.loading(classes: String? = null) {
        div("text-center ${classes ?: ""}") {
            div("spinner-border text-dark") {
                attrs.role = "status"
                span("sr-only") {
                    +"Loading..."
                }
            }
        }
    }

    fun hasFocus() = document.hasFocus()

    open fun onFocus() {}

    private val visibilityChangeCallback: ((Event) -> Unit) = {
        onFocus()
    }

    override fun componentDidMount() {
        window.addEventListener("focus", visibilityChangeCallback, false)
    }

    override fun componentWillUnmount() {
        window.removeEventListener("focus", visibilityChangeCallback, false)
    }

    fun showDialog(dialogBuilder: DialogBuilder) {
        setState {
            if (dialogs == null) {
                dialogs = mutableListOf()
            }
            dialogs!!.add(dialogBuilder)
        }
    }
}

fun <P : CoreRProps> RBuilder.coreChild(
    parent: CoreComponent<*, *>,
    klazz: KClass<out CoreComponent<P, *>>,
    handler: RHandler<P> = {}
): ReactElement {
    return klazz.rClass.invoke {
        attrs {
            this.context = parent.props.context
        }
        handler(this)
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

fun <P : CoreRProps, S : CoreRState> CoreComponent<P, S>.logger() = this.props.logger()

fun CoreRProps.logger() = this.context?.logger

fun <P : CoreRProps, S : CoreRState> CoreComponent<P, S>.rlogger() = this.props.rlogger()

fun CoreRProps.rlogger() = requireNotNull(this.context).logger