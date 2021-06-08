package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

class Pawn(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        val enemy = game.enemyOf(playerRequest)
        val direction = getDirection()

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

        getSpecialMove(playerRequest, game)?.let { possibleMoves.add(it) }
        return possibleMoves.clean(playerRequest, game)
    }

    override fun getSpecialMove(playerRequest: Player, game: Game): Position? {
        val isFirstMovement = isFirstMovement()
        val enemy = game.enemyOf(playerRequest)
        val direction = getDirection()

        if (isFirstMovement && !game.existPieceOn(next(0, 1 * direction), enemy)
            && !game.existPieceOn(next(0, 2 * direction), enemy)) {
            return next(0, 2 * direction)
        }

        return null
    }

    override fun canConvertPiece(): Boolean {
        val enemyYBaseLine = if (color == Color.WHITE) 8 else 1
        return position.y == enemyYBaseLine
    }

    private fun getDirection() = if (color == Color.WHITE) 1 else -1

    override fun clone(): Pawn {
        return Pawn(position, color)
    }

}
