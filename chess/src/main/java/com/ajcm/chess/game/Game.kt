package com.ajcm.chess.game

import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Board
import com.ajcm.chess.board.Position
import com.ajcm.chess.piece.Pawn
import com.ajcm.chess.piece.PawnTransform
import com.ajcm.chess.piece.Piece

interface Game {
    fun updateMovement(chessPiece: Piece, newPosition: Position, playerRequest: Player)
    fun enemyOf(playerRequest: Player): Player
    fun updateTurn()

    fun whoIsMoving(): Player

    // TODO - 3 functions that do same
    fun existPieceOn(position: Position, player: Player): Boolean
    fun getChessPieceFrom(player: Player, position: Position): Piece?
    fun isKingEnemyOn(position: Position, enemyPlayer: Player): Boolean

    // TODO - check if exits other simple way
    fun hasNoOwnMovements(playerRequest: Player, playerWaiting: Player): Boolean

    // TODO - Each piece must has their own logic of blocked, moves, hacked, etc, not the game
    fun isKingCheckedOf(playerRequest: Player, playerWaiting: Player, game: Game? = null): Boolean

    // TODO - same as existPieceOn but without player
    fun getPiecesOnBord(position: Position): Piece?
    fun getBoard(): Board

    fun isValidMadeFakeMovement(currentPosition: Position, newPosition: Position, playerRequest: Player): Boolean

    fun convert(currentPawn: Pawn, to: PawnTransform)
}
