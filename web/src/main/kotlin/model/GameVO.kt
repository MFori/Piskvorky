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
)

fun GameSnap.asGameVO() = GameVO(
    status = this.status,
    board = this.board,
    cross = this.cross,
    nought = this.nought,
    current = this.current,
    winner = this.winner
)