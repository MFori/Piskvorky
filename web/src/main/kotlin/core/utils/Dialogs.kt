package core.utils

import core.component.dialogBuilder
import react.RBuilder

/**
 * Dialog utils
 *
 * Created by Martin Forejt on 01.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun RBuilder.connectionErrorDialog(retry: () -> Unit) =
    dialogBuilder()
        .title("Connection error")
        .message("Connection error")
        .positiveBtn("Retry", retry)
        .negativeBtn(null,null)
        .build()
