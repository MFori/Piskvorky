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

class FriendsPanelProps : CoreRProps() {
    var users = mutableListOf<PublicUser>()
}

class FriendsPanel : CoreComponent<FriendsPanelProps, RState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            div("font-weight-bold") { +"Friends" }
           for (user in props.users) {
               div {
                   +user.email
               }
           }
        }
    }

}