package com.ajcm.chess.domain.piece

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Board
import com.ajcm.chess.domain.board.Coordinate
import com.ajcm.chess.domain.board.Position

abstract class Piece(position: Position, val color: Color) : Coordinate(position) {

    val initialPosition: Position = position

    val name: String = this::class.java.simpleName

    fun isFirstMovement(): Boolean = this.initialPosition == position

    abstract fun getPossibleMovements(playerRequest: Player, game: Game): List<Position>

    open fun getSpecialMove(playerRequest: Player, game: Game): Position? = null

    open fun canConvertPiece(): Boolean = false

    fun next(sumX: Int, sumY: Int) = Position(
        this.getX() + sumX,
        this.getY() + sumY
    )

    fun List<Position>.clean(player: Player, game: Game): List<Position> {
        return this.filter {
            !game.existPieceOn(it, player) && game.getBoard().containPosition(it)
        }
    }

    fun getDiagonalMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (d in diagonalMoves) {
            for (position in 1..Board.CELL_COUNT) {
                val newPosition = next(d.x * position, d.y * position)
                if (!game.existPieceOn(newPosition, playerRequest)) {
                    possibleMoves.add(newPosition)
                    if (game.existPieceOn(newPosition, game.enemyOf(playerRequest))) {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return possibleMoves
    }

    fun getLinealMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (d in linealMoves) {
            for (position in 1..Board.CELL_COUNT) {
                val newPosition = next(d.x * position, d.y * position)
                if (!game.existPieceOn(newPosition, playerRequest)) {
                    possibleMoves.add(newPosition)
                    if (game.existPieceOn(newPosition, game.enemyOf(playerRequest))) {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return possibleMoves
    }

    abstract fun clone(): Piece
}