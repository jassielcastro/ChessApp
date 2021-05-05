package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position

interface ChessPiece {
    val initialPosition: Position
    var currentPosition: Position
    val color: Color

    fun getX() = currentPosition.first
    fun getY() = currentPosition.second
    fun isFirstMovement(): Boolean = initialPosition == currentPosition
}
