package com.ajcm.chess.piece

import com.ajcm.chess.board.*
import com.ajcm.chess.ext.equals
import com.ajcm.chess.ext.next
import kotlinx.coroutines.launch

class King(
    override val player: Player
) : Piece(player),
    MovesDelegate by MovesDelegate.Impl() {

    /*
     * Avoid initial position value of flow.emit()
     */
    private var canUpdate: Boolean = false

    init {
        launch {
            position.collect {
                if (canUpdate && isInitialMove) {
                    moveIfCastling()
                }
                canUpdate = true
            }
        }
    }

    private suspend fun moveIfCastling() {
        val y = if (player.color == Color.WHITE) PositionConst.ONE else PositionConst.EIGHT
        val rook = player.availablePieces.value.filterIsInstance<Rook>()
            .firstOrNull { it.position.value equals Position(PositionConst.EIGHT, y) }
        rook?.position?.emit(Position(PositionConst.SIX, y))
    }

    override fun getPossibleMoves(): List<Position> {
        val moves = mutableListOf<Position>()
        val directions = mutableListOf<Position>()
        directions.addAll(diagonalMoves)
        directions.addAll(linealMoves)
        for (direction in directions) {
            moves.add(position.next(direction.x, direction.y))
        }
        moves.addAll(getSpecialMoves())
        return moves.toList()
    }

    override fun getSpecialMoves(): List<Position> {
        if (!isInitialMove) {
            return emptyList()
        }

        val rook = getSpecialRook()

        if (rook == null || !rook.isInitialMove) {
            return emptyList()
        }

        if (player.existPieceOn(Position(PositionConst.SIX, getSpecialY()))
            || player.existPieceOn(Position(PositionConst.SEVEN, getSpecialY()))
        ) {
            return emptyList()
        }

        return listOf(Position(PositionConst.SEVEN, getSpecialY())) // Castling movement
    }

    override fun copyWith(player: Player): Piece {
        return King(player)
    }

    private fun getSpecialRook(): Rook? {
        return player.availablePieces.value.filterIsInstance<Rook>().firstOrNull {
            it.position.value.x == PositionConst.EIGHT
        }
    }

    private fun getSpecialY(): Int = if (player.color == Color.WHITE) PositionConst.ONE else PositionConst.EIGHT

}
