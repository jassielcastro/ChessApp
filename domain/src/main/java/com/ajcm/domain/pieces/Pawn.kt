package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

class Pawn(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val isFirstMovement = isFirstMovement()
        val possibleMoves = mutableListOf<Position>()
        val enemy = game.getEnemyOf(playerRequest)
        val direction = if (color == Color.WHITE) 1 else -1
        if (isFirstMovement && !game.existPieceOn(next(0, 1 * direction), enemy)
            && !game.existPieceOn(next(0, 2 * direction), enemy)) {
            possibleMoves.add(next(0, 2 * direction))
        }
        if (!game.existPieceOn(next(0, 1 * direction), enemy)) {
            possibleMoves.add(next(0, 1 * direction))
        }
        if (game.existPieceOn(next(-1, 1 * direction), enemy)
            && !game.isKingEnemy(next(-1, 1 * direction), enemy)) {
            possibleMoves.add(next(-1, 1 * direction))
        }
        if (game.existPieceOn(next(1, 1 * direction), enemy)
            && !game.isKingEnemy(next(1, 1 * direction), enemy)) {
            possibleMoves.add(next(1, 1 * direction))
        }
        return possibleMoves.clean(playerRequest, game)
    }

    override fun clone(): Pawn {
        return Pawn(position, color)
    }

}
