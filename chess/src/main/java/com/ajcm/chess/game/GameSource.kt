package com.ajcm.chess.game

import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Board
import com.ajcm.chess.board.Position
import com.ajcm.chess.piece.*

class GameSource(
    private val playerOne: Player,
    private val playerTwo: Player,
    private val board: Board
) : Game {

    override fun updateMovement(chessPiece: Piece, newPosition: Position, playerRequest: Player) {
        chessPiece.position = newPosition

        if (chessPiece is King && newPosition.x == 7) {
            // Castling movement
            val y = if (playerRequest.color == Color.WHITE) 1 else 8
            getChessPieceFrom(playerRequest, Position(8, y))?.let { rook ->
                updateMovement(rook, Position(6, y), playerRequest)
            }
            return
        }

        enemyOf(playerRequest).apply {
            if (existPieceOn(newPosition, this) && !isKingEnemyOn(newPosition, this)) {
                getChessPieceFrom(this, newPosition)?.let { piece ->
                    availablePieces.remove(piece)
                }
            }
        }
    }

    override fun enemyOf(playerRequest: Player): Player =
        if (playerRequest.color == playerOne.color) playerTwo else playerOne

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

    override fun getChessPieceFrom(player: Player, position: Position): Piece? =
        player.availablePieces.find { position == it.position }

    override fun isKingEnemyOn(position: Position, enemyPlayer: Player): Boolean =
        getChessPieceFrom(enemyPlayer, position) is King

    override fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player): Boolean {
        return playerWaiting.availablePieces.map {
            Pair(it, it.getAllPossibleMovements(playerWaiting, this))
        }.filterNot {
            it.second.isEmpty()
        }.map {
            it.second.map { newPosition ->
                isValidMadeFakeMovement(it.first.position, newPosition, playerRequest)
            }
        }.flatten().all { it }
    }

    override fun isKingCheckedOf(
        playerRequest: Player,
        playerWaiting: Player,
        game: Game?
    ): Boolean {
        val kingPosition = getKingPositionFrom(playerWaiting)
        return playerRequest.availablePieces.any {
            it.getAllPossibleMovements(playerRequest, game ?: this).contains(kingPosition)
        }
    }

    private fun getKingPositionFrom(player: Player): Position? =
        player.availablePieces.filterIsInstance<King>().map {
            it.position
        }.firstOrNull()

    override fun isValidMadeFakeMovement(
        currentPosition: Position,
        newPosition: Position,
        playerRequest: Player
    ): Boolean {
        val player = enemyOf(playerRequest).copy()
        val enemyPlayerCopy = playerRequest.copy()

        val mockedPiece = getChessPieceFrom(player, currentPosition) ?: return false

        val mockedGame = GameSource(
            if (player.color == playerOne.color) player else enemyPlayerCopy,
            if (player.color == playerOne.color) enemyPlayerCopy else player,
            board
        )

        mockedPiece.position = newPosition
        if (existPieceOn(newPosition, enemyPlayerCopy) && !isKingEnemyOn(
                newPosition,
                enemyPlayerCopy
            )
        ) {
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

    override fun convert(currentPawn: Pawn, to: PawnTransform) {
        val piece = when (to) {
            PawnTransform.BISHOP -> Bishop(currentPawn.position, currentPawn.color)
            PawnTransform.KNIGHT -> Knight(currentPawn.position, currentPawn.color)
            PawnTransform.QUEEN -> Queen(currentPawn.position, currentPawn.color)
            PawnTransform.ROOK -> Rook(currentPawn.position, currentPawn.color)
        }

        with(whoIsMoving().availablePieces) {
            remove(currentPawn)
            add(piece)
        }
    }

}
