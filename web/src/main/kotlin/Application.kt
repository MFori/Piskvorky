import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Kermit
import cz.martinforejt.piskvorky.domain.core.di.initKoin
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.koin.core.KoinComponent
import react.child
import react.createContext
import react.dom.render
import view.AppComponent
import kotlin.coroutines.CoroutineContext

/**
 * Created by Martin Forejt on 27.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

object AppDependencies : KoinComponent {
    //  val repository: PeopleInSpaceRepository

    init {
        initKoin()
        //repository = get()
    }
}

val appContext = createContext<AppDependencies>()

class Application : CoroutineScope {

    companion object {
        val logger: Kermit = Kermit(CommonLogger())
    }

    override val coroutineContext: CoroutineContext = Job()

    fun start() {
        render(document.getElementById("root")) {
            appContext.Provider(AppDependencies) {
                child(AppComponent) {
                    attrs.coroutineScope = this@Application
                }
            }
        }
    }
}