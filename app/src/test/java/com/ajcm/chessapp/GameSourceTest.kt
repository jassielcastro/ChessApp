package com.ajcm.chessapp

import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Board
import com.ajcm.chess.domain.board.Position
import com.ajcm.chess.domain.piece.*
import com.ajcm.chess.game.GameSource
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameSourceTest {

    private val playerOne = Player(Color.WHITE)
    private val playerTwo = Player(Color.BLACK)
    private val game: Game = GameSource(playerOne, playerTwo, Board())

    @Test
    fun `check who is moving`() {
        assertEquals(game.whoIsMoving(), playerOne)
    }

    @Test
    fun `check who is waiting`() {
        assertEquals(game.enemyOf(game.whoIsMoving()), playerTwo)
    }

    @Test
    fun `check enemy pieces`() {
        assertTrue(game.existPieceOn(Position(1, 7), game.enemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(5, 7), game.enemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(4, 8), game.enemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(3, 8), game.enemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(8, 8), game.enemyOf(game.whoIsMoving())))
    }

    @Test
    fun `check own pieces`() {
        assertTrue(game.existPieceOn(Position(1, 1), game.whoIsMoving()))
        assertTrue(game.existPieceOn(Position(4, 2), game.whoIsMoving()))
        assertTrue(game.existPieceOn(Position(8, 2), game.whoIsMoving()))
        assertTrue(game.existPieceOn(Position(5, 1), game.whoIsMoving()))
        assertTrue(game.existPieceOn(Position(7, 2), game.whoIsMoving()))
    }

    @Test
    fun `check pieces from position`() {
        assertTrue(
            game.getChessPieceFrom(playerOne, Position(3, 1)) is Bishop
        )
    }

    @Test
    fun `update movements`() {
        assertTrue(game.existPieceOn(Position(3, 1), game.whoIsMoving()))
        val piece = game.getChessPieceFrom(game.whoIsMoving(), Position(3, 1))
        piece?.let {
            game.updateMovement(it, Position(5, 3), game.whoIsMoving())
            assertTrue(game.existPieceOn(Position(5, 3), game.whoIsMoving()))
        }
    }

    @Test
    fun `update movements and check enemy`() {
        assertTrue(game.existPieceOn(Position(3, 1), game.whoIsMoving()))
        val piece = game.getChessPieceFrom(game.whoIsMoving(), Position(3, 1))
        piece?.let {
            game.updateMovement(it, Position(5, 3), game.whoIsMoving())
            assertTrue(game.existPieceOn(Position(5, 3), game.whoIsMoving()))
            game.updateTurn()
            assertTrue(game.existPieceOn(Position(5, 3), game.enemyOf(game.whoIsMoving())))
        }
    }

    @Test
    fun `check initial White Pawn movement`() {
        val firstPawnMoves = playerOne.availablePieces[0].getPossibleMovements(playerOne, game)
        assertTrue(firstPawnMoves.size == 2)
    }

    @Test
    fun `check initial Black Pawn movement`() {
        val firstPawnMoves = playerTwo.availablePieces[0].getPossibleMovements(playerTwo, game)
        assertTrue(firstPawnMoves.size == 2)
    }

    @Test
    fun `check initial Knight movement`() {
        val firstKnightMoves = playerOne.availablePieces[10].getPossibleMovements(playerOne, game)
        assertEquals(firstKnightMoves.size, 2)
    }

    @Test
    fun `check all Knight movement`() {
        val firstKnightMoves = Knight(Position(5, 5), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstKnightMoves.size, 8)
    }

    @Test
    fun `check initial Bishop movement`() {
        val firstBishopMoves = Bishop(Position(3, 1), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstBishopMoves.size, 0)
    }

    @Test
    fun `check some Bishop movement`() {
        val firstBishopMoves = Bishop(Position(4, 4), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstBishopMoves.size, 8)
    }

    @Test
    fun `check initial Rook movement`() {
        val firstRookMoves = Rook(Position(1, 1), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstRookMoves.size, 0)
    }

    @Test
    fun `check some Rook movement`() {
        val firstRookMoves = Rook(Position(4, 5), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstRookMoves.size, 11)
    }

    @Test
    fun `check initial Queen movement`() {
        val firstQueenMoves = Queen(Position(4, 1), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstQueenMoves.size, 0)
    }

    @Test
    fun `check some Queen movement`() {
        val firstQueenMoves = Queen(Position(5, 4), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstQueenMoves.size, 19)
    }

    @Test
    fun `check initial King movement`() {
        val firstKingMoves = King(Position(5, 1), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstKingMoves.size, 0)
    }

    @Test
    fun `check some King movement`() {
        val firstKingMoves = King(Position(5, 4), Color.WHITE).getPossibleMovements(playerOne, game)
        assertEquals(firstKingMoves.size, 8)
    }

    @Test
    fun `is King checked initially`() {
        // initial move for WHITE
        assertFalse(game.isKingCheckedOf(game.enemyOf(game.whoIsMoving()), game.whoIsMoving(), game))
        // initial move for BLACK
        assertFalse(game.isKingCheckedOf(game.whoIsMoving(), game.enemyOf(game.whoIsMoving()), game))
    }

    @Test
    fun `is King checked`() {
        val piece = game.getChessPieceFrom(playerOne, Position(2, 1))
        piece?.let {
            game.updateMovement(it, Position(4, 6), playerOne)
            assertTrue(game.existPieceOn(Position(4, 6), playerOne))
            game.updateTurn()
            assertTrue(game.isKingCheckedOf(playerOne, playerTwo, game))
        }
    }

}
