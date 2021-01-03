package view

import core.component.CoreComponent
import core.component.CoreRProps
import cz.martinforejt.piskvorky.domain.model.PublicUser
import react.RBuilder
import react.RState
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

class OnlineUsersPanel : CoreComponent<OnlineUsersPanelProps, RState>() {

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