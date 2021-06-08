package com.ajcm.chess.domain.piece

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position

class Rook(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        return getLinealMovements(playerRequest, game).clean(playerRequest, game)
    }

    override fun clone(): Rook {
        return Rook(position, color)
    }

}
