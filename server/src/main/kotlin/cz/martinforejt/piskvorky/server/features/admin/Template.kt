package cz.martinforejt.piskvorky.server.features.admin

import io.ktor.html.*
import kotlinx.html.*

/**
 * Created by Martin Forejt on 09.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class LayoutTemplate: Template<HTML> {
    val header = Placeholder<FlowContent>()
    val content = TemplatePlaceholder<ContentTemplate>()

    override fun HTML.apply() {
        head {
            
        }
        body {
            h1 {
                insert(header)
            }
            insert(ContentTemplate(), content)
        }
    }
}

class ContentTemplate: Template<FlowContent> {
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