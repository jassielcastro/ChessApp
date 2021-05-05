package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position

data class Queen(
    override var currentPosition: Position,
    override val color: Color
) : ChessPiece {
    override val initialPosition: Position = currentPosition
}

