package com.ajcm.chess.domain.piece

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position

class Rook(position: Position, color: Color) : Piece(position, color) {

    override fun getAllPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        return getLinealMovements(playerRequest, game).removeInvalidMoves(playerRequest, game)
    }

    override fun clone(): Rook {
        return Rook(position, color)
    }

}
