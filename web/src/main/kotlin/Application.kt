import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Kermit
import core.component.AppContext
import core.component.AppContextHolder
import core.component.AppDependencies
import core.di.webModule
import cz.martinforejt.piskvorky.domain.core.di.initKoin
import kotlinx.browser.document
import org.koin.core.KoinComponent
import org.koin.core.inject
import react.child
import react.dom.render
import react.useContext
import view.AppComponent

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

object Dependencies : AppDependencies {
    override val logger: Kermit = Kermit(CommonLogger())
}

lateinit var appContext: AppContext

class Application : KoinComponent {

    fun start() {
        initKoin(listOf(webModule))

        val appContextHolder by inject<AppContextHolder>()
        appContext = appContextHolder.appContext

        render(document.getElementById("root")) {
            appContext.Provider(Dependencies) {
                child(AppComponent::class) {
                    attrs { context = Dependencies }
                }
            }
        }
    }
}