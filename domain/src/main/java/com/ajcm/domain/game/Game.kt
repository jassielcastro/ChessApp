package com.ajcm.domain.game

import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Position
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player

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
