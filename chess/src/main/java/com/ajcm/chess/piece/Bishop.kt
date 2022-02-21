package com.ajcm.chess.piece

import com.ajcm.chess.game.Game
import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position

class Bishop(position: Position, color: Color) : Piece(position, color) {

    override fun getAllPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        return getDiagonalMovements(playerRequest, game).removeInvalidMoves(playerRequest, game)
    }

    override fun clone(): Bishop {
        return Bishop(position, color)
    }

}
