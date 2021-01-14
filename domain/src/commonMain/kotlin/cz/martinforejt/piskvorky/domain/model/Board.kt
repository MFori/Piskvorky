package cz.martinforejt.piskvorky.domain.model

import cz.martinforejt.piskvorky.api.model.BoardCell
import cz.martinforejt.piskvorky.api.model.BoardValue

/**
 * ("Pseudo" infinity) Game board
 *
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class Board {

    private val cells = mutableMapOf<Pair<Int, Int>, BoardValue>()

    /**
     * Get symbol at positions
     */
    fun getValue(x: Int, y: Int) = cells[Pair(x, y)] ?: BoardValue.none

    /**
     * Get symbol at positions
     */
    operator fun get(x: Int, y: Int) = getValue(x, y)

    /**
     * Set symbol at position
     */
    fun setValue(x: Int, y: Int, value: BoardValue) {
        cells[Pair(x, y)] = value
    }

    /**
     * Set symbol at position
     */
    operator fun set(x: Int, y: Int, value: BoardValue) = setValue(x, y, value)

    /**
     * Is position empty
     */
    fun isEmpty(x: Int, y: Int) = getValue(x, y) == BoardValue.none

    /**
     * Is board empty
     */
    fun isEmpty() = cells.isEmpty()

    /**
     * Check if there is symbol near this position in any direction
     */
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

    /**
     * Map to api.model.Board
     */
    fun toApiBoard() = cz.martinforejt.piskvorky.api.model.Board(
        cells = this.cells.map { BoardCell(it.key.first, it.key.second, it.value) }
    )
}