package com.ajcm.domain.players

import com.ajcm.domain.board.Color
import com.ajcm.domain.game.Movement
import com.ajcm.domain.pieces.*

data class Player(val color: Color, var isMoving: Boolean) {
    val movesMade: MutableList<Movement> = mutableListOf()

    val availablePieces: MutableList<Piece> = mutableListOf(
        Piece.Pawn(if (color == Color.WHITE) Pair(1, 2) else Pair(1, 7) , color),
        Piece.Pawn(if (color == Color.WHITE) Pair(2, 2) else Pair(2, 7) , color),
        Piece.Pawn(if (color == Color.WHITE) Pair(3, 2) else Pair(3, 7) , color),
        Piece.Pawn(if (color == Color.WHITE) Pair(4, 2) else Pair(4, 7) , color),
        Piece.Pawn(if (color == Color.WHITE) Pair(5, 2) else Pair(5, 7) , color),
        Piece.Pawn(if (color == Color.WHITE) Pair(6, 2) else Pair(6, 7) , color),
        Piece.Pawn(if (color == Color.WHITE) Pair(7, 2) else Pair(7, 7) , color),
        Piece.Pawn(if (color == Color.WHITE) Pair(8, 2) else Pair(8, 7) , color),
        Piece.Bishop(if (color == Color.WHITE) Pair(3, 1) else Pair(3, 8) , color),
        Piece.Bishop(if (color == Color.WHITE) Pair(6, 1) else Pair(6, 8) , color),
        Piece.Knight(if (color == Color.WHITE) Pair(2, 1) else Pair(2, 8) , color),
        Piece.Knight(if (color == Color.WHITE) Pair(7, 1) else Pair(7, 8) , color),
        Piece.Rook(if (color == Color.WHITE) Pair(1, 1) else Pair(1, 8) , color),
        Piece.Rook(if (color == Color.WHITE) Pair(8, 1) else Pair(8, 8) , color),
        Piece.Queen(if (color == Color.WHITE) Pair(4, 1) else Pair(4, 8) , color),
        Piece.King(if (color == Color.WHITE) Pair(5, 1) else Pair(5, 8) , color)
    )
}
