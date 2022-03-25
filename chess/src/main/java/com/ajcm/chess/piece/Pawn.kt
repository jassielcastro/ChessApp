package com.ajcm.chess.piece

import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Position
import com.ajcm.chess.board.PositionConst
import com.ajcm.chess.ext.myEnemy
import com.ajcm.chess.ext.next
import kotlinx.coroutines.launch

class Pawn(
    override val player: Player
) : Piece(player) {

    init {
        initScope()
        if (!player.isFake) {
            observePawnEvolve()
        }
    }

    private fun observePawnEvolve() {
        launch {
            position.collect { pawnPosition ->
                val enemyYBaseLine =
                    if (player.color == Color.WHITE) PositionConst.EIGHT else PositionConst.ONE
                if (pawnPosition.y == enemyYBaseLine) {
                    player.evolvePawn.emit(this@Pawn)
                }
            }
        }
    }

    override fun getPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        val enemy = player.myEnemy()
        val direction = getDirection()

        if (!enemy.existPieceOn(position.next(0, 1 * direction))) {
            moves.add(position.next(0, 1 * direction))
        }
        if (enemy.existPieceOn(position.next(-1, 1 * direction))
            && !enemy.isKingOn(position.next(-1, 1 * direction))
        ) {
            moves.add(position.next(-1, 1 * direction))
        }
        if (enemy.existPieceOn(position.next(1, 1 * direction))
            && !enemy.isKingOn(position.next(1, 1 * direction))
        ) {
            moves.add(position.next(1, 1 * direction))
        }

        moves.addAll(getSpecialMoves())

        return moves.toList()
    }

    override fun getSpecialMoves(): List<Position> {
        val enemy = player.myEnemy()
        val direction = getDirection()

        if (isInitialMove && !enemy.existPieceOn(position.next(0, 1 * direction))
            && !player.existPieceOn(position.next(0, 1 * direction))
            && !enemy.existPieceOn(position.next(0, 2 * direction))
        ) {
            return listOf(position.next(0, 2 * direction))
        }

        return emptyList()
    }

    override fun copyWith(player: Player): Piece {
        return Pawn(player)
    }

    private fun getDirection() = if (player.color == Color.WHITE) 1 else -1

}
