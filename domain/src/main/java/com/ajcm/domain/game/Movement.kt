package com.ajcm.domain.game

import com.ajcm.domain.board.Position
import com.ajcm.domain.pieces.ChessPiece

typealias Removed = Pair<ChessPiece, Position>

class Movement(val chessPiece: ChessPiece) {
    lateinit var position: Position
    var removedEnemy: Removed? = null
}