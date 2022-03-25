package com.ajcm.chess.board

import com.ajcm.chess.ext.myEnemy
import com.ajcm.chess.ext.next
import com.ajcm.chess.piece.Piece

interface MovesDelegate {

    val diagonalMoves: List<Position>
    val linealMoves: List<Position>
    fun getMovesBy(piece: Piece, direction: List<Position>): List<Position>

    class Impl : MovesDelegate {
        override val diagonalMoves: List<Position> by lazy {
            listOf(Position(-1, -1), Position(-1, 1), Position(1, -1), Position(1, 1))
        }

        override val linealMoves: List<Position> by lazy {
            listOf(Position(-1, 0), Position(0, 1), Position(1, 0), Position(0, -1))
        }

        override fun getMovesBy(piece: Piece, direction: List<Position>): List<Position> {
            val possibleMoves = mutableListOf<Position>()
            for (d in direction) {
                for (p in 1..Board.CELL_COUNT) {
                    val newPosition = piece.position.next(d.x * p, d.y * p)
                    if (!piece.player.existPieceOn(newPosition)) {
                        possibleMoves.add(newPosition)
                        val enemy = piece.player.myEnemy()
                        if (enemy.existPieceOn(newPosition)) {
                            break
                        }
                    } else {
                        break
                    }
                }
            }
            return possibleMoves
        }
    }

}
