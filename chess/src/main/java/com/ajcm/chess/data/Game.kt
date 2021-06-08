package com.ajcm.chess.data

import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Board
import com.ajcm.chess.domain.board.Position
import com.ajcm.chess.domain.piece.Piece

interface Game {
    fun updateMovement(chessPiece: Piece, newPosition: Position, playerRequest: Player)
    fun enemyOf(playerRequest: Player): Player
    fun updateTurn()

    fun whoIsMoving(): Player
    fun existPieceOn(position: Position, player: Player): Boolean
    fun getChessPieceFrom(player: Player, position: Position): Piece?
    fun isKingEnemy(position: Position, enemyPlayer: Player): Boolean

    fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player): Boolean
    fun isKingCheckedOf(playerRequest: Player, playerWaiting: Player, game: Game? = null): Boolean

    fun getPiecesOnBord(position: Position): Piece?
    fun getBoard(): Board

    fun isValidMadeFakeMovement(currentPosition: Position, newPosition: Position, playerRequest: Player): Boolean
}
