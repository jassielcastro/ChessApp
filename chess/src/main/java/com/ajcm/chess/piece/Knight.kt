package com.ajcm.chess.piece

import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position
import com.ajcm.chess.ext.next

class Knight(
    override val player: Player
) : Piece(player) {

    override fun getPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        for (p in -2..2) {
            val y = if (p % 2 != 0) 2 else 1
            if (p != 0) {
                moves.add(position.next(p, y))
                moves.add(position.next(p, -1 * y))
            }
        }
        return moves.toList()
    }

    override fun copyWith(player: Player): Piece {
        return Knight(player)
    }

}
