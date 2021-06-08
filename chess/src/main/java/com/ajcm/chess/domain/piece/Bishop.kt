package com.ajcm.chess.domain.piece

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position

class Bishop(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        return getDiagonalMovements(playerRequest, game).clean(playerRequest, game)
    }

    override fun clone(): Bishop {
        return Bishop(position, color)
    }

}
