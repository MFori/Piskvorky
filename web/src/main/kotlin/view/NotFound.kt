package view

import react.*
import react.dom.h1

/**
 * Created by Martin Forejt on 26.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

val NotFound = functionalComponent<RProps> { _ ->
    h1 { +"404 - Not found" }
}