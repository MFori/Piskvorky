package cz.martinforejt.piskvorky.domain.model

import cz.martinforejt.piskvorky.api.model.BoardValue
import cz.martinforejt.piskvorky.api.model.GameSnap
import cz.martinforejt.piskvorky.api.model.Move
import cz.martinforejt.piskvorky.api.model.Player


/**
 * Created by Martin Forejt on 03.01.2021.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */
class Game(
    val cross: Player,
    val nought: Player,
    var current: BoardValue
) {
    val board = Board()
    var state = GameSnap.Status.running
    var winner = BoardValue.none

    companion object {
        const val WIN_COUNT = 5
    }

    fun giveUp(id: Int) {
        winner = rival(id)
        state = GameSnap.Status.end
    }

    fun play(id: Int, move: Move): Boolean {
        if (state != GameSnap.Status.running) return false
        if (current != value(id)) return false
        if (!board.isEmpty(move.x, move.y)) return false
        if (!board.isEmpty() && !board.isNear(move.x, move.y)) return false

        board[move.x, move.y] = current
        if (checkWinner(move, current)) {
            winner = current
            state = GameSnap.Status.end
        }
        current = current.revert()
        return true
    }

    fun player(id: Int): Player {
        return if (cross.id == id) {
            cross
        } else {
            nought
        }
    }

    fun rivalPlayer(id: Int): Player {
        return if (cross.id == id) {
            nought
        } else {
            cross
        }
    }

    fun value(id: Int): BoardValue {
        return if (cross.id == id) {
            BoardValue.cross
        } else {
            BoardValue.nought
        }
    }

    fun rival(id: Int): BoardValue {
        return if (cross.id != id) {
            BoardValue.cross
        } else {
            BoardValue.nought
        }
    }

    fun BoardValue.revert() = if (this == BoardValue.cross) BoardValue.nought else BoardValue.cross

    private fun checkWinner(move: Move, s: BoardValue): Boolean {
        //check col
        var count = 0
        for (i in move.y - WIN_COUNT until move.y + WIN_COUNT + 1) {
            if (board[move.x, i] != s) {
                count = 0
            } else {
                count++
            }
            if (count >= WIN_COUNT) {
                return true
            }
        }

        //check row
        count = 0
        for (i in move.x - WIN_COUNT until move.x + WIN_COUNT + 1) {
            if (board[i, move.y] != s) {
                count = 0
            } else {
                count++
            }
            if (count >= WIN_COUNT) {
                return true
            }
        }

        //check diag
        count = 0
        for (i in -WIN_COUNT until WIN_COUNT + 1) {
            if (board[move.x + i, move.y + i] != s) {
                count = 0
            } else {
                count++
            }
            if (count >= WIN_COUNT) {
                return true
            }
        }

        //check anti diag
        count = 0
        for (i in -WIN_COUNT until WIN_COUNT + 1) {
            if (board[move.x - i, move.y + i] != s) {
                count = 0
            } else {
                count++
            }
            if (count >= WIN_COUNT) {
                return true
            }
        }

        return false
    }
}

fun Game.toSnap() = GameSnap(
    status = this.state,
    board = this.board.toApiBoard(),
    cross = this.cross,
    nought = this.nought,
    winner = this.winner,
    current = this.current
)