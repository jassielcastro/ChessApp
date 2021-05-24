package com.ajcm.domain.board

abstract class Coordinate(var position: Position) {

    val diagonalMoves: List<Position> by lazy {
        listOf(Position(-1, -1), Position(-1, 1), Position(1, -1), Position(1, 1))
    }
    val linealMoves: List<Position> by lazy {
        listOf(Position(-1, 0), Position(0, 1), Position(1, 0), Position(0, -1))
    }

    fun getX() = position.first
    fun getY() = position.second
}