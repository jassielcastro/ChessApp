package com.ajcm.chess.domain.piece

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position

class Knight(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (position in -2..2) {
            val y = if (position % 2 != 0) 2 else 1
            if (position != 0) {
                possibleMoves.add(next(position, y))
                possibleMoves.add(next(position, -1 * y))
            }
        }
        return possibleMoves.clean(playerRequest, game)
    }

    override fun clone(): Knight {
        return Knight(position, color)
    }

}
