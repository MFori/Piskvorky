package view

import kotlinx.html.ButtonType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import kotlin.text.Typography.times

/**
 * Created by Martin Forejt on 13.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class HelpDialogProps : RProps {
    var dismissCallback: (() -> Unit)? = null
}

/**
 * Chat dialog component
 */
class HelpDialog : RComponent<HelpDialogProps, RState>() {

    override fun RBuilder.render() {
        div("core-dialog modal fade show") {
            attrs {
                id = "help-dialog"
                tabIndex = "-1"
                role = "dialog"
            }
            div("modal-dialog") {
                attrs.role = "document"
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            +"Help"
                        }
                        button(type = ButtonType.button, classes = "close") {
                            attrs.onClickFunction = {
                                props.dismissCallback?.invoke()
                            }
                            attrs {
                                set("data-dismiss", "modal")
                                set("aria-label", "Close")
                            }
                            span {
                                attrs {
                                    set("ara-hidden", "true")
                                }
                                +times.toString()
                            }
                        }
                    }
                    div("modal-body") {
                        b { +"Game controls" }
                        br {}
                        p {
                            +"- Header: it shows game players with info about you (bold name), game symbol and current player on move (black text and white background)"
                            br {}
                            +"- Footer: there are two buttons for chat dialog and for game give up"
                            br {}
                            +"- Grid buttons: grid button with icons for help (you know), centering the grid and zooming grid in and out"
                        }
                        b { +"Rules" }
                        br {}
                        p {
                            +"You must always place your symbol near any other symbol on grid (except first move), because of infinity grid and avoiding playing in 'other world'. "
                            br {}
                            +"Winner is player that first connect his 5 symbols in any direction."
                        }
                    }
                }
            }
        }
        div("modal-backdrop fade show") {}
    }
}