package com.ajcm.data

import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player

interface GameSource {
    fun getEnemyOf(player: Player, game: Game): Player
    fun updateMovement(chessPiece: Piece, newPosition: Position, playerRequest: Player, game: Game)
    fun updateTurn()

    fun whoIsMoving(game: Game): Player
    fun existPieceOn(position: Position, player: Player): Boolean
    fun getChessPieceFrom(player: Player, position: Position): Piece?
    fun isKingEnemy(position: Position, enemyPlayer: Player): Boolean

    fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player, game: Game): Boolean
    fun isKingCheckedOf(playerRequest: Player, playerWaiting: Player, game: Game): Boolean
}