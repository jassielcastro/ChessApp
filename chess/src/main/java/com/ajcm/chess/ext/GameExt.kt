package com.ajcm.chess.ext

import com.ajcm.chess.board.*
import com.ajcm.chess.piece.Piece
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

suspend fun MutableStateFlow<List<Piece>>.updateAndEmit(action: suspend (MutableList<Piece>) -> Unit) {
    val list = mutableListOf<Piece>()
    update {
        action(list)
        this.value + list.toList()
    }
}

fun Player.myEnemy(): Player = with(this.board) {
    return if (whitePlayer == this@myEnemy) blackPlayer else whitePlayer
}

infix fun Player.clean(possibleMoves: List<Position>): List<Position> {
    return possibleMoves.filter { position ->
        !availablePieces.value.map { piece -> piece.position.value }.contains(position)
    }.filter { position ->
        board.positions.contains(position)
    }
}

suspend fun <P : Piece> P.withPosition(
    whitePosition: Position,
    blackPosition: Position
): P {
    this.position.emit(if (player.color == Color.WHITE) whitePosition else blackPosition)
    return this
}

fun MutableStateFlow<Position>.next(x: X, y: Y): Position {
    return Position(this.value.x + x, this.value.y + y)
}

infix fun Position.equals(position: Position): Boolean {
    return this.x == position.x && this.y == position.y
}
