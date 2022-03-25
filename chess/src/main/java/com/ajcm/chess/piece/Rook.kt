package com.ajcm.chess.piece

import com.ajcm.chess.board.MovesDelegate
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position

class Rook(
    override val player: Player
) : Piece(player), MovesDelegate by MovesDelegate.Impl() {

    override fun getPossibleMoves(): List<Position> {
        return getMovesBy(this, linealMoves)
    }

    override fun copyWith(player: Player): Piece {
        return Rook(player)
    }

}
