package com.ajcm.chessapp.extensions

import com.ajcm.chessapp.R
import com.ajcm.domain.board.Color
import com.ajcm.domain.pieces.*

fun Piece.getImage(): Int = when (this) {
    is Bishop -> if (this.color == Color.BLACK) R.drawable.ic_bishop_black else R.drawable.ic_bishop_white
    is King -> if (this.color == Color.BLACK) R.drawable.ic_king_black else R.drawable.ic_king_white
    is Knight -> if (this.color == Color.BLACK) R.drawable.ic_horse_black else R.drawable.ic_horse_white
    is Pawn -> if (this.color == Color.BLACK) R.drawable.ic_pawn_black else R.drawable.ic_pawn_white
    is Queen -> if (this.color == Color.BLACK) R.drawable.ic_queen_black else R.drawable.ic_queen_white
    is Rook -> if (this.color == Color.BLACK) R.drawable.ic_rook_black else R.drawable.ic_rook_white
    else -> 0
}
