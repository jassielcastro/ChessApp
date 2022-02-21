package com.ajcm.chess.piece

import com.ajcm.chess.game.Game
import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position

class Knight(position: Position, color: Color) : Piece(position, color) {

    override fun getAllPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (position in -2..2) {
            val y = if (position % 2 != 0) 2 else 1
            if (position != 0) {
                possibleMoves.add(next(position, y))
                possibleMoves.add(next(position, -1 * y))
            }
        }
        return possibleMoves.removeInvalidMoves(playerRequest, game)
    }

    override fun clone(): Knight {
        return Knight(position, color)
    }

}
