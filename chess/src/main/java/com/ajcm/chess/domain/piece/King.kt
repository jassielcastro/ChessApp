package com.ajcm.chess.domain.piece

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position

class King(position: Position, color: Color) : Piece(position, color) {

    override fun getAllPossibleMovements(playerRequest: Player, game: Game): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        val directions = mutableListOf<Position>()
        directions.addAll(diagonalMoves)
        directions.addAll(linealMoves)
        for (direction in directions) {
            possibleMoves.add(next(direction.x, direction.y))
        }

        getSpecialMove(playerRequest, game)?.let { possibleMoves.add(it) }
        return possibleMoves.removeInvalidMoves(playerRequest, game)
    }

    override fun getSpecialMove(playerRequest: Player, game: Game): Position? {
        if (!isFirstMovement()) {
            return null
        }

        val rook = getSpecialRook(playerRequest)

        if (rook == null || !rook.isFirstMovement()) {
            return null
        }

        if (existPieceOn(6, playerRequest, game) || existPieceOn(7, playerRequest, game)) {
            return null
        }

        if (game.isValidMadeFakeMovement(position, Position(7, getSpecialY()), playerRequest)) {
            return null
        }

        return Position(7, getSpecialY()) // Castling movement
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