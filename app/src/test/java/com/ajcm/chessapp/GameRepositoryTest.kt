package com.ajcm.chessapp

import com.ajcm.chessapp.repository.GameRepositoryImpl
import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.pieces.*
import com.ajcm.domain.players.Player
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameRepositoryTest {

    private val playerOne = Player(Color.WHITE, true)
    private val playerTwo = Player(Color.BLACK, false)
    private val game: Game = Game(playerOne, playerTwo)
    private val gameRepositoryTest = GameRepositoryImpl(game, Board())

    @Test
    fun `check who is moving`() {
        assertEquals(gameRepositoryTest.whoIsMoving(), playerOne)
    }

    @Test
    fun `check who is waiting`() {
        assertEquals(gameRepositoryTest.whoIsWaiting(), playerTwo)
    }

    @Test
    fun `check enemy pieces`() {
        assertTrue(gameRepositoryTest.existPieceOn(Position(1, 7), gameRepositoryTest.whoIsWaiting()))
        assertTrue(gameRepositoryTest.existPieceOn(Position(5, 7), gameRepositoryTest.whoIsWaiting()))
        assertTrue(gameRepositoryTest.existPieceOn(Position(4, 8), gameRepositoryTest.whoIsWaiting()))
        assertTrue(gameRepositoryTest.existPieceOn(Position(3, 8), gameRepositoryTest.whoIsWaiting()))
        assertTrue(gameRepositoryTest.existPieceOn(Position(8, 8), gameRepositoryTest.whoIsWaiting()))
    }

    @Test
    fun `check own pieces`() {
        assertTrue(gameRepositoryTest.existPieceOn(Position(1, 1)))
        assertTrue(gameRepositoryTest.existPieceOn(Position(4, 2)))
        assertTrue(gameRepositoryTest.existPieceOn(Position(8, 2)))
        assertTrue(gameRepositoryTest.existPieceOn(Position(5, 1)))
        assertTrue(gameRepositoryTest.existPieceOn(Position(7, 2)))
    }

    @Test
    fun `check pieces from position`() {
        assertEquals(
            gameRepositoryTest.getChessPieceFrom(playerOne, Position(3, 1)),
            Bishop(Position(3, 1), Color.WHITE)
        )
    }

    @Test
    fun `update movements`() {
        assertTrue(gameRepositoryTest.existPieceOn(Position(3, 1)))
        gameRepositoryTest.updateMovement(Bishop(Position(3, 1), Color.WHITE), Position(5, 3))
        assertTrue(gameRepositoryTest.existPieceOn(Position(5, 3)))
    }

    @Test
    fun `update movements and check enemy`() {
        assertTrue(gameRepositoryTest.existPieceOn(Position(3, 1)))
        gameRepositoryTest.updateMovement(Bishop(Position(3, 1), Color.WHITE), Position(5, 3))
        assertTrue(gameRepositoryTest.existPieceOn(Position(5, 3)))
        gameRepositoryTest.updateTurn()
        assertTrue(gameRepositoryTest.existPieceOn(Position(5, 3), gameRepositoryTest.whoIsWaiting()))
    }

    @Test
    fun `check initial White Pawn movement`() {
        val firstPawnMoves = gameRepositoryTest.getPossibleMovementsOf(playerOne.availablePieces[0])
        assertTrue(firstPawnMoves.size == 2)
    }

    @Test
    fun `check initial Black Pawn movement`() {
        val firstPawnMoves = gameRepositoryTest.getPossibleMovementsOf(playerTwo.availablePieces[0])
        assertTrue(firstPawnMoves.size == 2)
    }

    @Test
    fun `check initial Knight movement`() {
        val firstKnightMoves = gameRepositoryTest.getPossibleMovementsOf(playerOne.availablePieces[10])
        assertEquals(firstKnightMoves.size, 2)
    }

    @Test
    fun `check all Knight movement`() {
        val firstKnightMoves = gameRepositoryTest.getPossibleMovementsOf(Knight(Position(5, 5), Color.WHITE))
        assertEquals(firstKnightMoves.size, 8)
    }

    @Test
    fun `check initial Bishop movement`() {
        val firstBishopMoves = gameRepositoryTest.getPossibleMovementsOf(Bishop(Position(3, 1), Color.WHITE))
        assertEquals(firstBishopMoves.size, 0)
    }

    @Test
    fun `check some Bishop movement`() {
        val firstBishopMoves = gameRepositoryTest.getPossibleMovementsOf(Bishop(Position(4, 4), Color.WHITE))
        assertEquals(firstBishopMoves.size, 8)
    }

    @Test
    fun `check initial Rook movement`() {
        val firstRookMoves = gameRepositoryTest.getPossibleMovementsOf(Rook(Position(1, 1), Color.WHITE))
        assertEquals(firstRookMoves.size, 0)
    }

    @Test
    fun `check some Rook movement`() {
        val firstRookMoves = gameRepositoryTest.getPossibleMovementsOf(Rook(Position(4, 5), Color.WHITE))
        assertEquals(firstRookMoves.size, 11)
    }

    @Test
    fun `check initial Queen movement`() {
        val firstQueenMoves = gameRepositoryTest.getPossibleMovementsOf(Queen(Position(4, 1), Color.WHITE))
        assertEquals(firstQueenMoves.size, 0)
    }

    @Test
    fun `check some Queen movement`() {
        val firstQueenMoves = gameRepositoryTest.getPossibleMovementsOf(Queen(Position(5, 4), Color.WHITE))
        assertEquals(firstQueenMoves.size, 19)
    }

    @Test
    fun `check initial King movement`() {
        val firstKingMoves = gameRepositoryTest.getPossibleMovementsOf(King(Position(5, 1), Color.WHITE))
        assertEquals(firstKingMoves.size, 0)
    }

    @Test
    fun `check some King movement`() {
        val firstKingMoves = gameRepositoryTest.getPossibleMovementsOf(King(Position(5, 4), Color.WHITE))
        assertEquals(firstKingMoves.size, 8)
    }

    @Test
    fun `check some King movement 2`() {
        gameRepositoryTest.updateMovement(Pawn(Position(5, 7), Color.BLACK), Position(5, 6), gameRepositoryTest.whoIsWaiting(), gameRepositoryTest.whoIsMoving())
        val firstKingMoves = gameRepositoryTest.getPossibleMovementsOf(King(Position(5, 8), Color.BLACK), gameRepositoryTest.whoIsWaiting(), gameRepositoryTest.whoIsMoving())
        assertEquals(firstKingMoves.size, 1)
    }

    @Test
    fun `is King checked initially`() {
        // initial move for WHITE
        assertFalse(gameRepositoryTest.isCheckedKingOf(gameRepositoryTest.whoIsWaiting(), gameRepositoryTest.whoIsMoving()))
        // initial move for BLACK
        assertFalse(gameRepositoryTest.isCheckedKingOf(gameRepositoryTest.whoIsMoving(), gameRepositoryTest.whoIsWaiting()))
    }

    @Test
    fun `is King checked`() {
        gameRepositoryTest.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6))
        assertTrue(gameRepositoryTest.existPieceOn(Position(4, 6)))
        gameRepositoryTest.updateTurn()
        assertTrue(gameRepositoryTest.isCheckedKingOf(gameRepositoryTest.whoIsMoving(), gameRepositoryTest.whoIsWaiting()))
    }

    @Test
    fun `is King dead`() {
        gameRepositoryTest.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6))
        assertTrue(gameRepositoryTest.existPieceOn(Position(4, 6)))
        gameRepositoryTest.updateTurn()
        val isChecked = gameRepositoryTest.isCheckedKingOf(gameRepositoryTest.whoIsMoving(), gameRepositoryTest.whoIsWaiting())
        val isDead = gameRepositoryTest.hasNoOwnMovements(gameRepositoryTest.whoIsMoving(), gameRepositoryTest.whoIsWaiting())
        assertTrue(isChecked && isDead)
    }

    @Test
    fun `is King is checked but with moves`() {
        gameRepositoryTest.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6))
        gameRepositoryTest.updateTurn()
        gameRepositoryTest.updateMovement(Knight(Position(5, 7), Color.BLACK), Position(5, 6))

        val isChecked = gameRepositoryTest.isCheckedKingOf(gameRepositoryTest.whoIsMoving(), gameRepositoryTest.whoIsWaiting())
        assertTrue(isChecked)
        val isDead = gameRepositoryTest.hasNoOwnMovements(gameRepositoryTest.whoIsMoving(), gameRepositoryTest.whoIsWaiting())
        assertFalse(isChecked && isDead)
    }
}
