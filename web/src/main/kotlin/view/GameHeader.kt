package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.coreChild
import cz.martinforejt.piskvorky.api.model.BoardValue
import kotlinx.html.id
import model.GameVO
import react.RBuilder
import react.RState
import react.dom.div
import react.dom.img
import react.dom.span

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class GameHeaderProps : CoreRProps() {
    var game: GameVO? = null
}

class GameHeader : CoreComponent<GameHeaderProps, RState>() {

    override fun RBuilder.render() {
        div("panel-box") {
            attrs {
                id = "header"
            }
            img(classes = "logo mg-4", src = "/images/logo2.png", alt = "logo") {}

            div {
                attrs.id = "game-title"
                div("player-box player-left ${if (props.game?.current == BoardValue.cross) "player-active" else ""}") {
                    img(src = "/icons/close.svg") {}
                    span(if(props.game?.cross?.email == user!!.email) "font-weight-bold" else "") {
                        +(props.game?.cross?.email ?: "")
                    }
                }
                div("player-box player-right ${if (props.game?.current == BoardValue.nought) "player-active" else ""}") {
                    img(src = "/icons/rec.svg") {}
                    span(if(props.game?.nought?.email == user!!.email) "font-weight-bold" else "") {
                        +(props.game?.nought?.email ?: "")
                    }
                }
            }
        }
    }

}

fun RBuilder.gameHeader(parent: CoreComponent<*, *>, game: GameVO?) =
    coreChild(parent, GameHeader::class) {
        attrs.game = game
    }