package com.ajcm.domain.game

import com.ajcm.domain.board.Position
import com.ajcm.domain.pieces.Piece

typealias Removed = Pair<Piece, Position>

class Movement(val piece: Piece) {
    lateinit var position: Position
    var removedEnemy: Removed? = null
}