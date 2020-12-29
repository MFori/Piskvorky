package core.utils

/**
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

fun <T> MutableList<T>.clearAndFill(data: Collection<T>) {
    this.clear()
    this.addAll(data)
}