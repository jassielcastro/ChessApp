package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

class King(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        val directions = mutableListOf<Position>()
        directions.addAll(diagonalMoves)
        directions.addAll(linealMoves)
        for (direction in directions) {
            possibleMoves.add(next(direction.first, direction.second))
        }
        return possibleMoves.clean(playerRequest, game)
    }

    override fun clone(): King {
        return King(position, color)
    }

}