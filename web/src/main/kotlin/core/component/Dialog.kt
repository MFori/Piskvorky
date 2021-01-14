package core.component

import kotlinx.html.ButtonType
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import react.*
import react.dom.*

/**
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class DialogProps : RProps {
    var title = ""
    var message = ""
    var positiveBtn: String? = null
    var negativeBtn: String? = null
    var positiveCallback: (() -> Unit)? = null
    var negativeCallback: (() -> Unit)? = null
    var dismissCallback: (() -> Unit)? = null
}

/**
 * Dialog component
 */
class Dialog : RComponent<DialogProps, RState>() {

    override fun RBuilder.render() {
        div("core-dialog modal fade show") {
            attrs {
                tabIndex = "-1"
                role = "dialog"
            }
            div("modal-dialog") {
                attrs.role = "document"
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            +props.title
                        }
                    }
                    div("modal-body") {
                        +props.message
                    }
                    div("modal-footer") {
                        if (props.negativeBtn != null) {
                            button(type = ButtonType.button, classes = "btn btn-secondary") {
                                attrs["data-dismiss"] = "modal"
                                attrs.onClickFunction = {
                                    props.negativeCallback?.invoke()
                                    props.dismissCallback?.invoke()
                                }
                                +props.negativeBtn!!
                            }
                        }
                        if (props.positiveBtn != null) {
                            button(type = ButtonType.button, classes = "btn btn-primary") {
                                attrs.onClickFunction = {
                                    props.positiveCallback?.invoke()
                                    props.dismissCallback?.invoke()
                                }
                                +props.positiveBtn!!
                            }
                        }
                    }
                }
            }
        }
        div("modal-backdrop fade show") {}
    }
}

/**
 * Dialog build
 *
 * @property builder Rbuilder to insert dialog into
 */
class DialogBuilder(private val builder: RBuilder? = null) {
    var title = ""
        private set
    var message = ""
        private set
    var positiveBtn: String? = null
        private set
    var negativeBtn: String? = null
        private set
    private var positiveCallback: (() -> Unit)? = null
    private var negativeCallback: (() -> Unit)? = null
    private var dismissCallback: (() -> Unit)? = null

    fun title(title: String) = apply { this.title = title }
    fun message(message: String) = apply { this.message = message }
    fun positiveBtn(title: String?, callback: (() -> Unit)?) =
        apply { positiveBtn = title; positiveCallback = callback }

    fun negativeBtn(title: String?, callback: (() -> Unit)?) =
        apply { negativeBtn = title; negativeCallback = callback }

    fun dismissCallback(dismissCallback: (() -> Unit)?) =
        apply { this.dismissCallback = dismissCallback }

    fun build(builder: RBuilder? = null): ReactElement {
        val b = builder ?: this.builder ?: throw IllegalStateException()
        return b.child(Dialog::class) {
            attrs {
                this.title = this@DialogBuilder.title
                this.message = this@DialogBuilder.message
                this.positiveBtn = this@DialogBuilder.positiveBtn
                this.negativeBtn = this@DialogBuilder.negativeBtn
                this.positiveCallback = this@DialogBuilder.positiveCallback
                this.negativeCallback = this@DialogBuilder.negativeCallback
                this.dismissCallback = this@DialogBuilder.dismissCallback
            }
        }
    }
}

/**
 * Create dialog in this Rbuilder
 */
fun RBuilder.dialogBuilder() = DialogBuilder(this)