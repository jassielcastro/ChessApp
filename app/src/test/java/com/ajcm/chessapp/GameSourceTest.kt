package com.ajcm.chessapp

import com.ajcm.chessapp.game.GameSourceImpl
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
class GameSourceTest {

    private val playerOne = Player(Color.WHITE)
    private val playerTwo = Player(Color.BLACK)
    private val game: Game = Game(playerOne, playerTwo, Board())
    private val gameRepositoryTest = GameSourceImpl(game)

    @Test
    fun `check who is moving`() {
        assertEquals(game.whoIsMoving(), playerOne)
    }

    @Test
    fun `check who is waiting`() {
        assertEquals(game.getEnemyOf(game.whoIsMoving()), playerTwo)
    }

    @Test
    fun `check enemy pieces`() {
        assertTrue(game.existPieceOn(Position(1, 7), game.getEnemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(5, 7), game.getEnemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(4, 8), game.getEnemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(3, 8), game.getEnemyOf(game.whoIsMoving())))
        assertTrue(game.existPieceOn(Position(8, 8), game.getEnemyOf(game.whoIsMoving())))
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
        gameRepositoryTest.updateMovement(Bishop(Position(3, 1), Color.WHITE), Position(5, 3), game.whoIsMoving(), game)
        assertTrue(game.existPieceOn(Position(5, 3), game.whoIsMoving()))
        assertEquals(game.whoIsMoving().movesMade.size, 1)
    }

    @Test
    fun `update movements and check enemy`() {
        assertTrue(game.existPieceOn(Position(3, 1), game.whoIsMoving()))
        gameRepositoryTest.updateMovement(Bishop(Position(3, 1), Color.WHITE), Position(5, 3), game.whoIsMoving(), game)
        assertTrue(game.existPieceOn(Position(5, 3), game.whoIsMoving()))
        gameRepositoryTest.updateTurn()
        assertTrue(game.existPieceOn(Position(5, 3), game.getEnemyOf(game.whoIsMoving())))
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
    fun `check some King movement 2`() {
        gameRepositoryTest.updateMovement(Pawn(Position(5, 7), Color.BLACK), Position(5, 6), game.getEnemyOf(game.whoIsMoving()), game)
        val firstKingMoves = King(Position(5, 8), Color.BLACK).getPossibleMovements(game.getEnemyOf(game.whoIsMoving()), game)
        assertEquals(firstKingMoves.size, 1)
    }

    @Test
    fun `is King checked initially`() {
        // initial move for WHITE
        assertFalse(gameRepositoryTest.isKingCheckedOf(gameRepositoryTest.getEnemyOf(game.whoIsMoving(), game), game.whoIsMoving(), game))
        // initial move for BLACK
        assertFalse(gameRepositoryTest.isKingCheckedOf(game.whoIsMoving(), gameRepositoryTest.getEnemyOf(game.whoIsMoving(), game), game))
    }

    @Test
    fun `is King checked`() {
        gameRepositoryTest.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6), playerOne, game)
        assertTrue(game.existPieceOn(Position(4, 6), playerOne))
        gameRepositoryTest.updateTurn()
        assertTrue(gameRepositoryTest.isKingCheckedOf(game.whoIsMoving(), gameRepositoryTest.getEnemyOf(game.whoIsMoving(), game), game))
    }

    @Test
    fun `is King dead`() {
        gameRepositoryTest.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6), playerOne, game)
        assertTrue(game.existPieceOn(Position(4, 6), playerOne))
        gameRepositoryTest.updateTurn()
        val isChecked = gameRepositoryTest.isKingCheckedOf(game.whoIsMoving(), gameRepositoryTest.getEnemyOf(game.whoIsMoving(), game), game)
        val isDead = gameRepositoryTest.hasNoOwnMovements(game.whoIsMoving(), gameRepositoryTest.getEnemyOf(game.whoIsMoving(), game), game)
        assertTrue(isChecked && isDead)
    }

    @Test
    fun `is King is checked but with moves`() {
        gameRepositoryTest.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6), playerOne, game)
        gameRepositoryTest.updateTurn()
        gameRepositoryTest.updateMovement(Knight(Position(5, 7), Color.BLACK), Position(5, 6), playerTwo, game)

        val isChecked = gameRepositoryTest.isKingCheckedOf(game.whoIsMoving(), gameRepositoryTest.getEnemyOf(game.whoIsMoving(), game), game)
        assertTrue(isChecked)
        val isDead = gameRepositoryTest.hasNoOwnMovements(game.whoIsMoving(), gameRepositoryTest.getEnemyOf(game.whoIsMoving(), game), game)
        assertFalse(isChecked && isDead)
    }
}
