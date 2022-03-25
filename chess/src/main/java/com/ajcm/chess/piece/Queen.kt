package com.ajcm.chess.piece

import com.ajcm.chess.board.MovesDelegate
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position

class Queen(
    override val player: Player
) : Piece(player),
    MovesDelegate by MovesDelegate.Impl() {

    override fun getPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        moves.addAll(getMovesBy(this, diagonalMoves))
        moves.addAll(getMovesBy(this, linealMoves))
        return moves.toList()
    }

    override fun copyWith(player: Player): Piece {
        return Queen(player)
    }

}
