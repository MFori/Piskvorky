package cz.martinforejt.piskvorky.server.features.admin.view

import cz.martinforejt.piskvorky.server.security.UserPrincipal
import io.ktor.html.*
import kotlinx.html.*

/**
 * Created by Martin Forejt on 09.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class AdminLayoutTempl<CT : AdminContentTempl>(
    private val contentTempl: CT,
    val user: UserPrincipal
) : Template<HTML> {
    val header = Placeholder<FlowContent>()
    val content = TemplatePlaceholder<CT>()

    override fun HTML.apply() {
        head {
            title("Piskvorky | Admin")
            link("admin/bootstrap.min.css", rel = "stylesheet")
        }
        body {
            h1 {
                insert(header)
            }
            insert(contentTempl, content)
        }
    }
}

interface AdminContentTempl : Template<FlowContent>

class ContentTemplate : Template<FlowContent> {
    val articleTitle = Placeholder<FlowContent>()
    val articleText = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        article {
            h2 {
                insert(articleTitle)
            }
            p {
                insert(articleText)
            }
        }
    }
}