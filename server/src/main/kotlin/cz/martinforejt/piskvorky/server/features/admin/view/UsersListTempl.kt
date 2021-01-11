package cz.martinforejt.piskvorky.server.features.admin.view

import cz.martinforejt.piskvorky.domain.model.User
import kotlinx.html.FlowContent
import kotlinx.html.HTML

/**
 * Created by Martin Forejt on 11.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class UsersListTempl(
    val users: List<User>
) : AdminContentTempl {

    override fun FlowContent.apply() {
        TODO("Not yet implemented")
    }


}