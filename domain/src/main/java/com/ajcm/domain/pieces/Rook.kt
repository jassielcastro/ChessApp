package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

class Rook(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        return getLinealMovements(playerRequest, game).clean(playerRequest, game)
    }

    override fun clone(): Rook {
        return Rook(position, color)
    }

}
