package com.ajcm.chess.piece

import com.ajcm.chess.game.Game
import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position

class Queen(position: Position, color: Color) : Piece(position, color) {

    override fun getAllPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        possibleMoves.addAll(getDiagonalMovements(playerRequest, game))
        possibleMoves.addAll(getLinealMovements(playerRequest, game))
        return possibleMoves.removeInvalidMoves(playerRequest, game)
    }

    override fun clone(): Queen {
        return Queen(position, color)
    }

}
