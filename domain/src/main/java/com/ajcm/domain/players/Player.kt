package com.ajcm.domain.players

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Movement
import com.ajcm.domain.board.Position
import com.ajcm.domain.pieces.*

data class Player(val color: Color) {

    var isMoving: Boolean = false
    val movesMade: MutableList<Movement> = mutableListOf()

    val availablePieces: MutableList<Piece> = mutableListOf(
        Pawn(if (color == Color.WHITE) Position(1, 2) else Position(1, 7) , color),
        Pawn(if (color == Color.WHITE) Position(2, 2) else Position(2, 7) , color),
        Pawn(if (color == Color.WHITE) Position(3, 2) else Position(3, 7) , color),
        Pawn(if (color == Color.WHITE) Position(4, 2) else Position(4, 7) , color),
        Pawn(if (color == Color.WHITE) Position(5, 2) else Position(5, 7) , color),
        Pawn(if (color == Color.WHITE) Position(6, 2) else Position(6, 7) , color),
        Pawn(if (color == Color.WHITE) Position(7, 2) else Position(7, 7) , color),
        Pawn(if (color == Color.WHITE) Position(8, 2) else Position(8, 7) , color),
        Bishop(if (color == Color.WHITE) Position(3, 1) else Position(3, 8) , color),
        Bishop(if (color == Color.WHITE) Position(6, 1) else Position(6, 8) , color),
        Knight(if (color == Color.WHITE) Position(2, 1) else Position(2, 8) , color),
        Knight(if (color == Color.WHITE) Position(7, 1) else Position(7, 8) , color),
        Rook(if (color == Color.WHITE) Position(1, 1) else Position(1, 8) , color),
        Rook(if (color == Color.WHITE) Position(8, 1) else Position(8, 8) , color),
        Queen(if (color == Color.WHITE) Position(4, 1) else Position(4, 8) , color),
        King(if (color == Color.WHITE) Position(5, 1) else Position(5, 8) , color)
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
