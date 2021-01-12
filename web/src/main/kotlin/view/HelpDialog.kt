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
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.span
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
                        +"help content"
                    }
                }
            }
        }
        div("modal-backdrop fade show") {}
    }
}