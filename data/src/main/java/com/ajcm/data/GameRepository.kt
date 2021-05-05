package com.ajcm.data

import com.ajcm.domain.board.Position
import com.ajcm.domain.pieces.ChessPiece
import com.ajcm.domain.players.Player

interface GameRepository {
    fun whoIsMoving(): Player
    fun whoIsWaiting(): Player
    fun getEnemyOf(player: Player): Player
    fun updateMovement(chessPiece: ChessPiece, newPosition: Position, playerRequest: Player = whoIsMoving())
    fun updateTurn()
    fun existPieceOn(position: Position, player: Player = whoIsMoving()): Boolean
    fun getChessPieceFrom(player: Player, position: Position): ChessPiece?
    fun getPossibleMovementsOf(chessPiece: ChessPiece, playerRequest: Player = whoIsMoving()): List<Position>
    fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player): Boolean
    fun isCheckedKingOf(playerRequest: Player, playerWaiting: Player): Boolean
}
