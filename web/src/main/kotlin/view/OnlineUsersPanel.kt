package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import cz.martinforejt.piskvorky.domain.model.PublicUser
import react.RBuilder
import react.dom.div

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class OnlineUsersPanelProps : CoreRProps() {
    var users: MutableList<PublicUser>? = null
    var onAction: ((PlayerListItem.Action, PlayerVO) -> Unit)? = null
}

/**
 * Online users component
 */
class OnlineUsersPanel : CoreComponent<OnlineUsersPanelProps, CoreRState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            div("panel-title") { +"Online users" }
            if (props.users == null) {
                loading()
            } else {
                for (user in props.users!!) {
                    playerListItem(this@OnlineUsersPanel, user.toPlayerVO(), props.onAction)
                }
            }
        }
    }

    private fun PublicUser.toPlayerVO() = PlayerVO(id, email, online, inGame,false)

}