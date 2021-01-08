package model

import cz.martinforejt.piskvorky.api.model.Board
import cz.martinforejt.piskvorky.api.model.BoardValue
import cz.martinforejt.piskvorky.api.model.GameSnap
import cz.martinforejt.piskvorky.api.model.Player

/**
 * Created by Martin Forejt on 04.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
data class GameVO(
    val status: GameSnap.Status,
    val board: Board,
    val cross: Player,
    val nought: Player,
    val current: BoardValue,
    val winner: BoardValue
) {

    private val cells: MutableMap<Pair<Int, Int>, BoardValue> = mutableMapOf()

    init {

        board.cells.forEach { cell ->
            cells[Pair(cell.x, cell.y)] = cell.value
        }
    }

    fun getValue(x: Int, y: Int) = cells[Pair(x, y)] ?: BoardValue.none

    operator fun get(x: Int, y: Int) = getValue(x, y)

    fun setValue(x: Int, y: Int, value: BoardValue) {
        cells[Pair(x, y)] = value
    }

    operator fun set(x: Int, y: Int, value: BoardValue) = setValue(x, y, value)

    fun isEmpty(x: Int, y: Int) = getValue(x, y) == BoardValue.none

    fun player(email: String) = if (cross.email == email) BoardValue.cross else BoardValue.nought
}

fun GameSnap.asGameVO() = GameVO(
    status = this.status,
    board = this.board,
    cross = this.cross,
    nought = this.nought,
    current = this.current,
    winner = this.winner
)