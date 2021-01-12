package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.CoreRState
import core.component.coreChild
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.RBuilder
import react.dom.a
import react.dom.button
import react.dom.div
import react.dom.span

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class GameFooterProps : CoreRProps() {
    var messages: Int = 0
    var onGiveUpClicked: ((Event) -> Unit)? = null
    var onChatClicked: ((Event) -> Unit)? = null
}

/**
 * Game footer component
 */
class GameFooter : CoreComponent<GameFooterProps, CoreRState>() {

    override fun RBuilder.render() {
        div("panel-box game-footer") {
            attrs {
                id = "footer"
            }
            button(classes = "btn btn-dark chat") {
                props.onChatClicked?.let { attrs.onClickFunction = it }
                +"Chat (${props.messages})"
            }
            span(classes = "copy") {
                +"@ 2020 "
                a(href = "https://martinforejt.cz", target = "_blank") {
                    +"Martin Forejt"
                }
            }
            button(classes = "btn btn-dark giveup") {
                props.onGiveUpClicked?.let { attrs.onClickFunction = it }
                +"GiveUp"
            }
        }
    }

}

fun RBuilder.gameFooter(
    parent: CoreComponent<*, *>,
    messages: Int,
    onGiveUp: (Event) -> Unit,
    onChat: (Event) -> Unit
) =
    coreChild(parent, GameFooter::class) {
        attrs.messages = messages
        attrs.onGiveUpClicked = onGiveUp
        attrs.onChatClicked = onChat
    }