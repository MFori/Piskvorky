package cz.martinforejt.piskvorky.server.features.admin.view

import kotlinx.html.*

/**
 * Created by Martin Forejt on 11.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class HomePageTempl : AdminContentTempl {
    override fun FlowContent.apply() {
        article {
            h2 {
                +"Hello from admin zone."
            }
            div {
                a("/admin/users") {+"Users"}
            }
            div {
                a("/admin/results") {+"Results"}
            }
        }
    }
}