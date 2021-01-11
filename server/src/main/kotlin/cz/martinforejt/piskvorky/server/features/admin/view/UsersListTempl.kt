package cz.martinforejt.piskvorky.server.features.admin.view

import cz.martinforejt.piskvorky.domain.model.User
import kotlinx.html.*

/**
 * Created by Martin Forejt on 11.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class UsersListTempl(
    private val users: List<User>
) : AdminContentTempl {

    override fun FlowContent.apply() {
        h1 {
            +"Users list"
        }

        table("table table-striped mt-4") {
            thead("thead-dark") {
                tr {
                    th { +"ID" }
                    th { +"Email" }
                    th { +"Created" }
                    th { +"Admin" }
                    th { +"Active" }
                    th { +"Edit" }
                }
            }
            tbody {
                users.forEach { user ->
                    userRow(user)
                }
            }
        }
    }

    private fun TBODY.userRow(user: User) {
        tr {
            td { +"${user.id}" }
            td { +user.email }
            td { +"${user.created}" }
            td { +(if(user.admin) "yes" else "no") }
            td { +(if(user.active) "yes" else "no") }
            td { a("/admin/users/${user.id}") { +"Edit" } }
        }
    }

}