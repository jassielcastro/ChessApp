package com.ajcm.domain.pieces

import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Coordinate
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

abstract class Piece(position: Position, val color: Color) : Coordinate(position) {

    private val initialPosition: Position = position

    fun isFirstMovement(): Boolean = this.initialPosition == position

    abstract fun getPossibleMovements(playerRequest: Player, game: Game): List<Position>

    fun next(sumX: Int, sumY: Int) = Position(
        this.getX() + sumX,
        this.getY() + sumY
    )

    fun List<Position>.clean(player: Player, game: Game): List<Position> {
        return this.filter {
            !game.existPieceOn(it, player) && game.board.containPosition(it)
        }
    }

    fun getDiagonalMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (d in diagonalMoves) {
            for (position in 1..Board.CELL_COUNT) {
                val newPosition = next(d.first * position, d.second * position)
                if (!game.existPieceOn(newPosition, playerRequest)) {
                    possibleMoves.add(newPosition)
                    if (game.existPieceOn(newPosition, game.getEnemyOf(playerRequest))) {
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
                val newPosition = next(d.first * position, d.second * position)
                if (!game.existPieceOn(newPosition, playerRequest)) {
                    possibleMoves.add(newPosition)
                    if (game.existPieceOn(newPosition, game.getEnemyOf(playerRequest))) {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return possibleMoves
    }

}