package cz.martinforejt.piskvorky.server.features.admin.view

import cz.martinforejt.piskvorky.domain.model.User
import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.html.*
import kotlinx.html.*

/**
 * Created by Martin Forejt on 11.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class HomePageTempl(
    val users: List<User>
) : AdminContentTempl {
    val hovnoText = Placeholder<FlowContent>()
    val users2 = PlaceholderList<FlowContent, User>()
    override fun FlowContent.apply() {
        article {
            h2 {
                insert(hovnoText)
            }
            users.forEach {
                p {
                    +"${it.email}"
                }
            }
        }
    }
}