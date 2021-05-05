package com.ajcm.chessapp.repository

import com.ajcm.data.GameRepository
import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.game.Movement
import com.ajcm.domain.game.Removed
import com.ajcm.domain.pieces.*
import com.ajcm.domain.players.Player

class GameRepositoryImpl(private val game: Game, private val board: Board) : GameRepository {

    private val diagonalMoves = listOf(Position(-1, -1), Position(-1, 1), Position(1, -1), Position(1, 1))
    private val linealMoves = listOf(Position(-1, 0), Position(0, 1), Position(1, 0), Position(0, -1))

    override fun whoIsMoving(): Player = if (game.playerOne.isMoving) {
        game.playerOne
    } else {
        game.playerTwo
    }

    override fun whoIsWaiting(): Player = if (game.playerOne.isMoving) {
        game.playerTwo
    } else {
        game.playerOne
    }

    override fun getEnemyOf(player: Player): Player = if (player == game.playerOne) game.playerTwo else game.playerOne

    override fun updateMovement(chessPiece: ChessPiece, newPosition: Position, playerRequest: Player) {
        with(playerRequest) {
            val movement = Movement(chessPiece)
            getChessPieceFrom(this, chessPiece.currentPosition)?.let {
                it.currentPosition = newPosition
                movement.position = newPosition
            } ?: return
            getEnemyOf(playerRequest).apply {
                if (existPieceOn(newPosition, this) && !isKingEnemy(newPosition)) {
                    getChessPieceFrom(this, newPosition)?.let { piece ->
                        availablePieces.remove(piece)
                        movement.removedEnemy = Removed(piece, newPosition)
                    }
                }
            }
            playerRequest.movesMade.add(movement)
        }
    }

    override fun updateTurn() {
        with(whoIsMoving()) {
            whoIsWaiting().apply {
                isMoving = true
            }
            isMoving = false
        }
    }

    override fun existPieceOn(position: Position, player: Player): Boolean = with(player) {
        availablePieces.map { it.currentPosition }.contains(position)
    }

    override fun getChessPieceFrom(player: Player, position: Position): ChessPiece? =
        player.availablePieces.find { position == it.currentPosition }

    override fun getPossibleMovementsOf(chessPiece: ChessPiece, playerRequest: Player): List<Position> {
        return when(chessPiece) {
            is Pawn -> pawnMovements(chessPiece)
            is Bishop -> bishopMovements(chessPiece, playerRequest)
            is Knight -> knightMovements(chessPiece)
            is Rook -> rookMovements(chessPiece, playerRequest)
            is Queen -> queenMovements(chessPiece, playerRequest)
            is King -> kingMovements(chessPiece)
            else -> emptyList()
        }.clean(playerRequest)
    }

    override fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player): Boolean {
        return getKingPositionFrom(playerRequest)?.let { kingPosition ->
            getChessPieceFrom(playerRequest, kingPosition)?.let { king ->
                val availableMovesOfKing = getPossibleMovementsOf(king)
                val enemyMoves = playerWaiting.availablePieces.map {
                    getPossibleMovementsOf(it, playerWaiting)
                }.flatten()
                if (availableMovesOfKing.isEmpty() && enemyMoves.contains(kingPosition)) {
                    return true
                }
                availableMovesOfKing.all {
                    enemyMoves.contains(it)
                }
            } ?: false
        } ?: false
    }

    override fun isCheckedKingOf(playerRequest: Player, playerWaiting: Player): Boolean = with(playerWaiting) {
        val kingPosition = getKingPositionFrom(playerRequest)
        return this.availablePieces.any {
            getPossibleMovementsOf(it, playerWaiting).contains(kingPosition)
        }
    }

    /***
     * Internal functions of pieces
     */

    private fun getKingPositionFrom(player: Player): Position? =
        player.availablePieces.filterIsInstance<King>().map { it.currentPosition }.firstOrNull()

    private fun pawnMovements(chessPiece: ChessPiece): List<Position> {
        val isFirstMovement = chessPiece.isFirstMovement()
        val possibleMoves = mutableListOf<Position>()
        val direction = if (chessPiece.color == Color.WHITE) 1 else -1
        if (isFirstMovement && !existPieceOn(chessPiece.next(0, 1 * direction), whoIsWaiting())
            && !existPieceOn(chessPiece.next(0, 2 * direction), whoIsWaiting())) {
            possibleMoves.add(chessPiece.next(0, 2 * direction))
        }
        if (!existPieceOn(chessPiece.next(0, 1 * direction), whoIsWaiting())) {
            possibleMoves.add(chessPiece.next(0, 1 * direction))
        }
        if (existPieceOn(chessPiece.next(-1, 1 * direction), whoIsWaiting())
            && !isKingEnemy(chessPiece.next(-1, 1 * direction))) {
            possibleMoves.add(chessPiece.next(-1, 1 * direction))
        }
        if (existPieceOn(chessPiece.next(1, 1 * direction), whoIsWaiting())
            && !isKingEnemy(chessPiece.next(1, 1 * direction))) {
            possibleMoves.add(chessPiece.next(1, 1 * direction))
        }
        return possibleMoves
    }

    private fun bishopMovements(chessPiece: ChessPiece, playerRequest: Player): List<Position> {
        return getDiagonalMovements(chessPiece, playerRequest)
    }

    private fun knightMovements(chessPiece: ChessPiece): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (position in -2..2) {
            val y = if (position % 2 != 0) 2 else 1
            if (position != 0) {
                possibleMoves.add(chessPiece.next(position, y))
                possibleMoves.add(chessPiece.next(position, -1 * y))
            }
        }
        return possibleMoves
    }

    private fun rookMovements(chessPiece: ChessPiece, playerRequest: Player): List<Position> {
        return getLinealMovements(chessPiece, playerRequest)
    }

    private fun queenMovements(chessPiece: ChessPiece, playerRequest: Player): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        possibleMoves.addAll(getDiagonalMovements(chessPiece, playerRequest))
        possibleMoves.addAll(getLinealMovements(chessPiece, playerRequest))
        return possibleMoves
    }

    private fun kingMovements(chessPiece: ChessPiece): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        val directions = mutableListOf<Position>()
        directions.addAll(diagonalMoves)
        directions.addAll(linealMoves)
        for (direction in directions) {
            possibleMoves.add(chessPiece.next(direction.first, direction.second))
        }
        return possibleMoves
    }

    private fun ChessPiece.next(sumX: Int, sumY: Int) = Position(
        this.getX() + sumX,
        this.getY() + sumY
    )

    private fun List<Position>.clean(player: Player): List<Position> {
        return this.filter {
            !existPieceOn(it, player) && board.containPosition(it)
        }
    }

    private fun isKingEnemy(position: Position): Boolean
        = getChessPieceFrom(whoIsWaiting(), position) is King

    private fun getDiagonalMovements(chessPiece: ChessPiece, playerRequest: Player): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (d in diagonalMoves) {
            for (position in 1..Board.CELL_COUNT) {
                val newPosition = chessPiece.next(d.first * position, d.second * position)
                if (!existPieceOn(newPosition, playerRequest)) {
                    possibleMoves.add(newPosition)
                    if (existPieceOn(newPosition, getEnemyOf(playerRequest))) {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return possibleMoves
    }

    private fun getLinealMovements(chessPiece: ChessPiece, playerRequest: Player): List<Position> {
        val possibleMoves = mutableListOf<Position>()
        for (d in linealMoves) {
            for (position in 1..Board.CELL_COUNT) {
                val newPosition = chessPiece.next(d.first * position, d.second * position)
                if (!existPieceOn(newPosition, playerRequest)) {
                    possibleMoves.add(newPosition)
                    if (existPieceOn(newPosition, getEnemyOf(playerRequest))) {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return possibleMoves
    }

}
