package com.ajcm.chess.board

class Board {

    companion object {
        const val CELL_COUNT = 8
        const val COLUMN_COUNT = 8
    }

    val positions: List<Position> by lazy {
        val mutableList = mutableListOf<Position>()
        for (positionX in 1..CELL_COUNT) {
            for (positionY in 1..COLUMN_COUNT) {
                mutableList.add(Position(positionX, positionY))
            }
        }
        mutableList.toList()
    }

    fun containPosition(position: Position): Boolean = positions.contains(position)
}
