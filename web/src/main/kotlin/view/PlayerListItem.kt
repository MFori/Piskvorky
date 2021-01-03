package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.coreChild
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import react.RBuilder
import react.RState
import react.dom.button
import react.dom.div
import react.dom.img
import react.dom.span

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

data class PlayerVO(
    val id: Int,
    val email: String,
    val online: Boolean,
    val inGame: Boolean,
    val friend: Boolean
)

class PlayerListItemProps : CoreRProps() {
    var user: PlayerVO? = null
    var onAction: ((PlayerListItem.Action, PlayerVO) -> Unit)? = null
}

class PlayerListItem : CoreComponent<PlayerListItemProps, RState>() {

    override fun RBuilder.render() {
        val player = props.user ?: return
        div("player-list-item") {
            span("status-dot ${if (player.online) "status-online" else "status-offline"}") {}
            span(classes = "player-name") {
                +player.email
            }
            button(classes = "btn-player btn-add") {
                img("add", src = if (player.friend) "/icons/person-dash-fill.svg" else "/icons/person-plus-fill.svg") {
                    attrs {
                        width = "28"
                        height = "28"
                        title = if (player.friend) "Remove from friends" else "Add to friends"
                    }
                }
                attrs {
                    onClickFunction = {
                        props.onAction?.invoke(if (player.friend) Action.REMOVE_FRIEND else Action.ADD_FRIEND, player)
                    }
                }
            }
            if(player.online && !player.inGame){
                button(classes = "btn-player btn-play") {
                    img("add", src = "/icons/play-fill.svg") {
                        attrs {
                            width = "28"
                            height = "28"
                            title = "Play"
                        }
                    }
                    attrs {
                        onClickFunction = {
                            props.onAction?.invoke(Action.PLAY, player)
                        }
                    }
                }
            }
        }
    }

    enum class Action {
        ADD_FRIEND,
        REMOVE_FRIEND,
        PLAY
    }
}

fun RBuilder.playerListItem(parent: CoreComponent<*, *>, user: PlayerVO, onAction: ((PlayerListItem.Action, PlayerVO) -> Unit)?) =
    coreChild(parent, PlayerListItem::class) {
        attrs.user = user
        attrs.onAction = onAction
    }