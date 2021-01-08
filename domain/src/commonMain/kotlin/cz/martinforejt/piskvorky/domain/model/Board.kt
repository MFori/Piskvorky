package cz.martinforejt.piskvorky.domain.model

import cz.martinforejt.piskvorky.api.model.BoardCell
import cz.martinforejt.piskvorky.api.model.BoardValue

/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class Board {

    private val cells = mutableMapOf<Pair<Int, Int>, BoardValue>()

    fun getValue(x: Int, y: Int) = cells[Pair(x, y)] ?: BoardValue.none

    operator fun get(x: Int, y: Int) = getValue(x, y)

    fun setValue(x: Int, y: Int, value: BoardValue) {
        cells[Pair(x, y)] = value
    }

    operator fun set(x: Int, y: Int, value: BoardValue) = setValue(x, y, value)

    fun isEmpty(x: Int, y: Int) = getValue(x, y) == BoardValue.none

    fun isEmpty() = cells.isEmpty()

    fun isNear(x: Int, y: Int): Boolean {
        return if (!isEmpty(x - 1, y - 1)) true
        else if (!isEmpty(x, y - 1)) true
        else if (!isEmpty(x + 1, y - 1)) true
        else if (!isEmpty(x + 1, y)) true
        else if (!isEmpty(x + 1, y + 1)) true
        else if (!isEmpty(x, y + 1)) true
        else if (!isEmpty(x - 1, y + 1)) true
        else !isEmpty(x - 1, y)
    }

    fun toApiBoard() = cz.martinforejt.piskvorky.api.model.Board(
        cells = this.cells.map { BoardCell(it.key.first, it.key.second, it.value) }
    )
}