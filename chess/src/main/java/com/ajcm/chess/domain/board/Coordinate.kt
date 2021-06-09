package com.ajcm.chess.domain.board

abstract class Coordinate(var position: Position) {

    internal val diagonalMoves: List<Position> by lazy {
        listOf(Position(-1, -1), Position(-1, 1), Position(1, -1), Position(1, 1))
    }
    internal val linealMoves: List<Position> by lazy {
        listOf(Position(-1, 0), Position(0, 1), Position(1, 0), Position(0, -1))
    }

    internal fun getX() = position.x
    internal fun getY() = position.y
}
