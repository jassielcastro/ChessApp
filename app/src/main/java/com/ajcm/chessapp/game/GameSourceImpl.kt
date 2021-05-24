package com.ajcm.chessapp.game

import com.ajcm.data.GameSource
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.game.Movement
import com.ajcm.domain.game.Removed
import com.ajcm.domain.pieces.King
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player

class GameSourceImpl(private val games: Game) : GameSource {

    override fun getEnemyOf(player: Player, game: Game): Player = if (player == game.playerOne) game.playerTwo else game.playerOne

    override fun updateMovement(chessPiece: Piece, newPosition: Position, playerRequest: Player, game: Game) {
        val movement = Movement(chessPiece)
        chessPiece.position = newPosition
        movement.position = newPosition

        getEnemyOf(playerRequest, game).apply {
            if (existPieceOn(newPosition, this) && !isKingEnemy(newPosition, this)) {
                getChessPieceFrom(this, newPosition)?.let { piece ->
                    availablePieces.remove(piece)
                    movement.removedEnemy = Removed(piece, newPosition)
                }
            }
        }
        playerRequest.movesMade.add(movement)
    }

    override fun updateTurn() {
        with(whoIsMoving(games)) {
            getEnemyOf(this, games).apply {
                isMoving = true
            }
            isMoving = false
        }
    }

    override fun whoIsMoving(game: Game): Player = if (game.playerOne.isMoving) game.playerOne else game.playerTwo

    override fun existPieceOn(position: Position, player: Player): Boolean = with(player) {
        availablePieces.map { it.position }.contains(position)
    }

    override fun getChessPieceFrom(player: Player, position: Position): Piece? = player.availablePieces.find { position == it.position }

    override fun isKingEnemy(position: Position, enemyPlayer: Player): Boolean = getChessPieceFrom(enemyPlayer, position) is King

    override fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player, game: Game): Boolean {
        return playerWaiting.availablePieces.map {
            Pair(it, it.getPossibleMovements(playerWaiting, game))
        }.filterNot {
            it.second.isEmpty()
        }.map {
            it.second.map { newPosition ->
                isValidMadeFakeMovement(it.first.position, newPosition, playerRequest, playerWaiting, game)
            }
        }.flatten().all { it }
    }

    override fun isKingCheckedOf(playerRequest: Player, playerWaiting: Player, game: Game): Boolean {
        val kingPosition = getKingPositionFrom(playerWaiting)
        return playerRequest.availablePieces.any {
            it.getPossibleMovements(playerRequest, game).contains(kingPosition)
        }
    }

    private fun getKingPositionFrom(player: Player): Position? =
        player.availablePieces.filterIsInstance<King>().map {
            it.position }.firstOrNull()

    fun isValidMadeFakeMovement(currentPosition: Position, newPosition: Position, playerRequest: Player, playerWaiting: Player, game: Game): Boolean {
        val player = playerWaiting.copy()
        val enemyPlayerCopy = playerRequest.copy()
        val mockedGame = Game(
            if (player.color == game.playerOne.color) player else enemyPlayerCopy,
            if (player.color == game.playerOne.color) enemyPlayerCopy else player,
            game.board
        )

        val mockedPiece = getChessPieceFrom(player, currentPosition) ?: return false

        mockedPiece.position = newPosition
        if (existPieceOn(newPosition, enemyPlayerCopy) && !isKingEnemy(newPosition, enemyPlayerCopy)) {
            getChessPieceFrom(enemyPlayerCopy, newPosition)?.let { piece ->
                enemyPlayerCopy.availablePieces.remove(piece)
            }
        }
        return isKingCheckedOf(enemyPlayerCopy, player, mockedGame)
    }

}
