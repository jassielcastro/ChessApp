package com.ajcm.chess.domain.piece

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position

class Queen(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        possibleMoves.addAll(getDiagonalMovements(playerRequest, game))
        possibleMoves.addAll(getLinealMovements(playerRequest, game))
        return possibleMoves.clean(playerRequest, game)
    }

    override fun clone(): Queen {
        return Queen(position, color)
    }

}
