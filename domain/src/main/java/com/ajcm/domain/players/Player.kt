package com.ajcm.domain.players

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Movement
import com.ajcm.domain.pieces.*

data class Player(val color: Color) {

    var isMoving: Boolean = false
    val movesMade: MutableList<Movement> = mutableListOf()

    val availablePieces: MutableList<Piece> = mutableListOf(
        Pawn(if (color == Color.WHITE) Pair(1, 2) else Pair(1, 7) , color),
        Pawn(if (color == Color.WHITE) Pair(2, 2) else Pair(2, 7) , color),
        Pawn(if (color == Color.WHITE) Pair(3, 2) else Pair(3, 7) , color),
        Pawn(if (color == Color.WHITE) Pair(4, 2) else Pair(4, 7) , color),
        Pawn(if (color == Color.WHITE) Pair(5, 2) else Pair(5, 7) , color),
        Pawn(if (color == Color.WHITE) Pair(6, 2) else Pair(6, 7) , color),
        Pawn(if (color == Color.WHITE) Pair(7, 2) else Pair(7, 7) , color),
        Pawn(if (color == Color.WHITE) Pair(8, 2) else Pair(8, 7) , color),
        Bishop(if (color == Color.WHITE) Pair(3, 1) else Pair(3, 8) , color),
        Bishop(if (color == Color.WHITE) Pair(6, 1) else Pair(6, 8) , color),
        Knight(if (color == Color.WHITE) Pair(2, 1) else Pair(2, 8) , color),
        Knight(if (color == Color.WHITE) Pair(7, 1) else Pair(7, 8) , color),
        Rook(if (color == Color.WHITE) Pair(1, 1) else Pair(1, 8) , color),
        Rook(if (color == Color.WHITE) Pair(8, 1) else Pair(8, 8) , color),
        Queen(if (color == Color.WHITE) Pair(4, 1) else Pair(4, 8) , color),
        King(if (color == Color.WHITE) Pair(5, 1) else Pair(5, 8) , color)
    )

    init {
        if (this.color == Color.WHITE) {
            isMoving = true
        }
    }

    fun copy(): Player {
        val newPlayer = Player(color)
        newPlayer.availablePieces.clear()
        availablePieces.map {
            newPlayer.availablePieces.add(it.clone())
        }
        newPlayer.isMoving = isMoving
        return newPlayer
    }
}
