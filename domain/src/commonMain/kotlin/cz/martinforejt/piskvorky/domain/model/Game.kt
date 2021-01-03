package cz.martinforejt.piskvorky.domain.model

import cz.martinforejt.piskvorky.api.model.BoardValue
import cz.martinforejt.piskvorky.api.model.GameSnap
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
    val current: BoardValue
) {
    val board = Board()
    val state = GameSnap.Status.running
    val winner = BoardValue.none
}

fun Game.toSnap() = GameSnap(
    status = this.state,
    board = this.board.toApiBoard(),
    cross = this.cross,
    nought = this.nought,
    winner = this.winner,
    current = this.current
)