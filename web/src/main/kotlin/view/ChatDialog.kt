package view

import cz.martinforejt.piskvorky.domain.model.ChatMessage
import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*
import kotlin.js.Date
import kotlin.text.Typography.times

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class ChatDialogProps : RProps {
    var messages: List<ChatMessage>? = null
    var onSend: ((String) -> Unit)? = null
    var dismissCallback: (() -> Unit)? = null
}

class ChatDialogState : RState {
    var message: String = ""
}

class ChatDialog : RComponent<ChatDialogProps, ChatDialogState>() {

    override fun ChatDialogState.init() {
        message = ""
    }

    override fun RBuilder.render() {
        div("core-dialog modal fade show") {
            attrs {
                id = "chat-dialog"
                tabIndex = "-1"
                role = "dialog"
            }
            div("modal-dialog") {
                attrs.role = "document"
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            +"Chat"
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
                        props.messages?.forEach {
                            div {
                                //span { +"${it.date}" }
                                b { +" ${it.from}: " }
                                +it.message
                            }
                        }
                    }
                    div("modal-footer") {
                        div("container") {
                            div("row") {
                                div("col-9") {
                                    input(type = InputType.text, classes = "form-control") {
                                        attrs.placeholder = "Type your message..."
                                        attrs.value = state.message
                                        attrs.onChangeFunction = {
                                            setState { message = (it.target as HTMLInputElement).value }
                                        }
                                    }
                                }
                                div("col-3") {
                                    button(type = ButtonType.button, classes = "btn btn-primary btn-block") {
                                        attrs.onClickFunction = {
                                            props.onSend?.invoke(state.message)
                                            setState { message = "" }
                                        }
                                        +"Send"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        div("modal-backdrop fade show") {}
    }
}