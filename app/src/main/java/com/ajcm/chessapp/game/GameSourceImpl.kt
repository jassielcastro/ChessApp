package com.ajcm.chessapp.game

import com.ajcm.domain.game.Game
import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Movement
import com.ajcm.domain.board.Position
import com.ajcm.domain.board.Removed
import com.ajcm.domain.pieces.King
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player

class GameSourceImpl(private val playerOne: Player, private val playerTwo: Player, private val board: Board) : Game {

    override fun updateMovement(chessPiece: Piece, newPosition: Position, playerRequest: Player) {
        val movement = Movement(chessPiece)
        chessPiece.position = newPosition
        movement.position = newPosition

        enemyOf(playerRequest).apply {
            if (existPieceOn(newPosition, this) && !isKingEnemy(newPosition, this)) {
                getChessPieceFrom(this, newPosition)?.let { piece ->
                    availablePieces.remove(piece)
                    movement.removedEnemy = Removed(piece, newPosition)
                }
            }
        }
        playerRequest.movesMade.add(movement)
    }

    override fun enemyOf(playerRequest: Player): Player = if (playerRequest.color == playerOne.color) playerTwo else playerOne

    override fun updateTurn() {
        with(whoIsMoving()) {
            enemyOf(this).apply {
                isMoving = true
            }
            isMoving = false
        }
    }

    override fun whoIsMoving(): Player = if (playerOne.isMoving) playerOne else playerTwo

    override fun existPieceOn(position: Position, player: Player): Boolean = with(player) {
        availablePieces.map { it.position }.contains(position)
    }

    override fun getChessPieceFrom(player: Player, position: Position): Piece? = player.availablePieces.find { position == it.position }

    override fun isKingEnemy(position: Position, enemyPlayer: Player): Boolean = getChessPieceFrom(enemyPlayer, position) is King

    override fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player): Boolean {
        return playerWaiting.availablePieces.map {
            Pair(it, it.getPossibleMovements(playerWaiting, this))
        }.filterNot {
            it.second.isEmpty()
        }.map {
            it.second.map { newPosition ->
                isValidMadeFakeMovement(it.first.position, newPosition, playerRequest)
            }
        }.flatten().all { it }
    }

    override fun isKingCheckedOf(playerRequest: Player, playerWaiting: Player, game: Game?): Boolean {
        val kingPosition = getKingPositionFrom(playerWaiting)
        return playerRequest.availablePieces.any {
            it.getPossibleMovements(playerRequest, game ?: this).contains(kingPosition)
        }
    }

    private fun getKingPositionFrom(player: Player): Position? =
        player.availablePieces.filterIsInstance<King>().map {
            it.position }.firstOrNull()

    override fun isValidMadeFakeMovement(currentPosition: Position, newPosition: Position, playerRequest: Player): Boolean {
        val player = enemyOf(playerRequest).copy()
        val enemyPlayerCopy = playerRequest.copy()

        val mockedPiece = getChessPieceFrom(player, currentPosition) ?: return false

        val mockedGame = GameSourceImpl(
            if (player.color == playerOne.color) player else enemyPlayerCopy,
            if (player.color == playerOne.color) enemyPlayerCopy else player,
            board
        )

        mockedPiece.position = newPosition
        if (existPieceOn(newPosition, enemyPlayerCopy) && !isKingEnemy(newPosition, enemyPlayerCopy)) {
            getChessPieceFrom(enemyPlayerCopy, newPosition)?.let { piece ->
                enemyPlayerCopy.availablePieces.remove(piece)
            }
        }
        return isKingCheckedOf(enemyPlayerCopy, player, mockedGame)
    }

    override fun getPiecesOnBord(position: Position): Piece? =
        playerOne.availablePieces.find { position == it.position }
            ?: playerTwo.availablePieces.find { position == it.position }

    override fun getBoard(): Board = board

}
