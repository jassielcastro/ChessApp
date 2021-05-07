package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position

sealed class Piece(private val initialPosition: Position, private val color: Color) {
    var currentPosition: Position = initialPosition

    data class Bishop(val initialPosition: Position, val teamColor: Color): Piece(initialPosition, teamColor)
    data class King(val initialPosition: Position, val teamColor: Color): Piece(initialPosition, teamColor)
    data class Knight(val initialPosition: Position, val teamColor: Color): Piece(initialPosition, teamColor)
    data class Pawn(val initialPosition: Position, val teamColor: Color): Piece(initialPosition, teamColor)
    data class Queen(val initialPosition: Position, val teamColor: Color): Piece(initialPosition, teamColor)
    data class Rook(val initialPosition: Position, val teamColor: Color): Piece(initialPosition, teamColor)

    fun getX() = currentPosition.first
    fun getY() = currentPosition.second
    fun isFirstMovement(): Boolean = this.initialPosition == currentPosition
    fun getColor() = this.color
}
