package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

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
