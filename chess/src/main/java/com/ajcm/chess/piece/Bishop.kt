package com.ajcm.chess.piece

import com.ajcm.chess.board.MovesDelegate
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position

class Bishop(
    override val player: Player
) : Piece(player), MovesDelegate by MovesDelegate.Impl() {

    override fun getPossibleMoves(): List<Position> {
        return getMovesBy(this, diagonalMoves)
    }

    override fun copyWith(player: Player): Piece {
        return Bishop(player)
    }

}
