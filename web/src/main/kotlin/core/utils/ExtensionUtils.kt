package core.utils

/**
 * Extension utils
 *
 * Created by Martin Forejt on 29.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

/**
 * Clear list and insert all [data]
 */
fun <T> MutableList<T>.clearAndFill(data: Collection<T>) {
    this.clear()
    this.addAll(data)
}