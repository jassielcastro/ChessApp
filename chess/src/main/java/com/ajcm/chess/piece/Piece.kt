package com.ajcm.chess.piece

import com.ajcm.chess.game.Game
import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Board
import com.ajcm.chess.board.Coordinate
import com.ajcm.chess.board.Position

abstract class Piece(position: Position, val color: Color) : Coordinate(position) {

    private val initialPosition: Position = position

    val name: String = this::class.java.simpleName

    fun isFirstMovement(): Boolean = this.initialPosition == position

    internal abstract fun getAllPossibleMovements(playerRequest: Player, game: Game): List<Position>

    fun getPossibleMoves(playerRequest: Player, game: Game): List<Position> {
        val moves = this.getAllPossibleMovements(playerRequest, game)
        val validMoves = mutableListOf<Position>()
        for (position in moves) {
            if (!game.isValidMadeFakeMovement(this.position, position, game.enemyOf(playerRequest))) {
                validMoves.add(position)
            }
        }
        return validMoves.toList()
    }

    internal open fun getSpecialMove(playerRequest: Player, game: Game): Position? = null

    open fun canConvertPiece(): Boolean = false

    internal fun next(sumX: Int, sumY: Int) = Position(
        this.getX() + sumX,
        this.getY() + sumY
    )

    internal fun List<Position>.removeInvalidMoves(player: Player, game: Game): List<Position> {
        return this.filter {
            !game.existPieceOn(it, player) && game.getBoard().containPosition(it)
        }
    }

    internal fun getDiagonalMovements(playerRequest: Player, game: Game): List<Position> {
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

    internal fun getLinealMovements(playerRequest: Player, game: Game): List<Position> {
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

    internal abstract fun clone(): Piece
}
