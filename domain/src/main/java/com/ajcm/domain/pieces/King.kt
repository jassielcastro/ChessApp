package com.ajcm.domain.pieces

import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

class King(position: Position, color: Color) : Piece(position, color) {

    override fun getPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        val directions = mutableListOf<Position>()
        directions.addAll(diagonalMoves)
        directions.addAll(linealMoves)
        for (direction in directions) {
            possibleMoves.add(next(direction.x, direction.y))
        }

        possibleMoves.addAll(getSpecialMoves(playerRequest, game))
        return possibleMoves.clean(playerRequest, game)
    }

    override fun getSpecialMoves(playerRequest: Player, game: Game): List<Position> {
        if (game.isKingCheckedOf(playerRequest, game.enemyOf(playerRequest))) {
            return emptyList()
        }

        if (!isFirstMovement()) {
            return emptyList()
        }

        val rook = getSpecialRook(playerRequest)

        if (rook == null || !rook.isFirstMovement()) {
            return emptyList()
        }

        if (existPieceOn(6, playerRequest, game) || existPieceOn(7, playerRequest, game)) {
            return emptyList()
        }

        if (!game.isValidMadeFakeMovement(position, Position(7, getSpecialY()), playerRequest)) {
            return emptyList()
        }

        return listOf(Position(7, getSpecialY())) // Castling movement
    }

    private fun existPieceOn(x: Int, playerRequest: Player, game: Game): Boolean {
        return game.existPieceOn(Position(x, getSpecialY()), playerRequest)
    }

    private fun getSpecialY(): Int = if (color == Color.WHITE) 1 else 8

    private fun getSpecialRook(playerRequest: Player): Rook? {
        return playerRequest.availablePieces.filterIsInstance<Rook>().firstOrNull {
            it.position.x == 8
        }
    }

    override fun clone(): King {
        return King(position, color)
    }

}