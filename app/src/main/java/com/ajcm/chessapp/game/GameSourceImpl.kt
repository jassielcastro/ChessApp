package com.ajcm.chessapp.game

import com.ajcm.data.GameSource
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.game.Movement
import com.ajcm.domain.game.Removed
import com.ajcm.domain.pieces.*
import com.ajcm.domain.players.Player

class GameSourceImpl(private val game: Game) : GameSource {

    override fun getEnemyOf(player: Player): Player = game.getEnemyOf(player)

    override fun updateMovement(chessPiece: Piece, newPosition: Position, playerRequest: Player) {
        with(playerRequest) {
            val movement = Movement(chessPiece)
            game.getChessPieceFrom(this, chessPiece.position)?.let {
                it.position = newPosition
                movement.position = newPosition
            } ?: return

            getEnemyOf(playerRequest).apply {
                if (game.existPieceOn(newPosition, this) && !game.isKingEnemy(newPosition, this)) {
                    game.getChessPieceFrom(this, newPosition)?.let { piece ->
                        availablePieces.remove(piece)
                        movement.removedEnemy = Removed(piece, newPosition)
                    }
                }
            }
            playerRequest.movesMade.add(movement)
        }
    }

    override fun updateTurn() {
        with(game.whoIsMoving()) {
            game.getEnemyOf(this).apply {
                isMoving = true
            }
            isMoving = false
        }
    }

    override fun whoIsMoving(): Player = game.whoIsMoving()

    override fun existPieceOn(position: Position, player: Player): Boolean = game.existPieceOn(position, player)

    override fun getChessPieceFrom(player: Player, position: Position): Piece? = game.getChessPieceFrom(player, position)

    override fun isKingEnemy(position: Position, enemyPlayer: Player): Boolean = game.isKingEnemy(position, enemyPlayer)

    override fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player): Boolean {
        return getKingPositionFrom(playerRequest)?.let { kingPosition ->
            game.getChessPieceFrom(playerRequest, kingPosition)?.let { king ->
                val availableMovesOfKing = king.getPossibleMovements(playerRequest, game)
                val enemyMoves = playerWaiting.availablePieces.map {
                    it.getPossibleMovements(playerWaiting, game)
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

    override fun isKingCheckedOf(playerRequest: Player, playerWaiting: Player): Boolean {
        val kingPosition = getKingPositionFrom(playerWaiting)
        return playerRequest.availablePieces.any {
            it.getPossibleMovements(playerRequest, game).contains(kingPosition)
        }
    }

    private fun getKingPositionFrom(player: Player): Position? =
        player.availablePieces.filterIsInstance<King>().map {
            it.position }.firstOrNull()

}
