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
interface AdminContentTempl : Template<FlowContent>

class AdminLayoutTempl<CT : AdminContentTempl>(
    private val contentTempl: CT,
    val user: UserPrincipal
) : Template<HTML> {
    val pageTitle = Placeholder<TITLE>()
    val content = TemplatePlaceholder<CT>()

    override fun HTML.apply() {
        head {
            title { insert(pageTitle) }
            meta("viewport", "width=device-width, initial-scale=1, shrink-to-fit=no")
            link("/admin/bootstrap.min.css", rel = "stylesheet")
            link("/admin/admin.css", rel = "stylesheet")
        }
        body {
            createHeader()
            article {
                insert(contentTempl, content)
            }
        }
    }

    private fun BODY.createHeader() {
        header {
            a("/admin") {
                +"Piskvorky-Admin"
            }
            div {
                id = "user-box"
                +user.email
                +" | "
                a("/logout") {
                    +"logout"
                }
            }
        }
    }
}