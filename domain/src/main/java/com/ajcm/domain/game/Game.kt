package com.ajcm.domain.game

import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.pieces.King
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player

class Game(val playerOne: Player, val playerTwo: Player, val board: Board) {
    init {
        if (playerOne.isMoving && playerTwo.isMoving) {
            throw IllegalArgumentException("Just one Player can move at time")
        }
        if (playerOne.color == playerTwo.color) {
            throw IllegalArgumentException("Select a different color by each Player")
        }
    }

    fun whoIsMoving(): Player = if (playerOne.isMoving) playerOne else playerTwo

    fun getEnemyOf(player: Player): Player = if (player == playerOne) playerTwo else playerOne

    fun existPieceOn(position: Position, player: Player): Boolean = with(player) {
        availablePieces.map { it.position }.contains(position)
    }

    fun getChessPieceFrom(player: Player, position: Position): Piece? =
        player.availablePieces.find { position == it.position }

    fun isKingEnemy(position: Position, enemyPlayer: Player): Boolean =
        getChessPieceFrom(enemyPlayer, position) is King

    fun getPiecesOnBord(position: Position): Piece? =
        playerOne.availablePieces.find { position == it.position }
            ?: playerTwo.availablePieces.find { position == it.position }

    fun getPlayerBy(color: Color): Player = if (playerOne.color == color) playerOne else playerTwo

}
