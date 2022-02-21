package com.ajcm.chess.piece

import com.ajcm.chess.game.Game
import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position

class Rook(position: Position, color: Color) : Piece(position, color) {

    override fun getAllPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        return getLinealMovements(playerRequest, game).removeInvalidMoves(playerRequest, game)
    }

    override fun clone(): Rook {
        return Rook(position, color)
    }

}
