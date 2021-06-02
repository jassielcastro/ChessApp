package com.ajcm.chessapp

import com.ajcm.chessapp.game.GameSourceImpl
import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.pieces.*
import com.ajcm.domain.players.Player
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameSourceTest {

    private val playerOne = Player(Color.WHITE)
    private val playerTwo = Player(Color.BLACK)
    private val game: Game = GameSourceImpl(playerOne, playerTwo, Board())

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
        game.updateMovement(Bishop(Position(3, 1), Color.WHITE), Position(5, 3), game.whoIsMoving())
        assertTrue(game.existPieceOn(Position(5, 3), game.whoIsMoving()))
        assertEquals(game.whoIsMoving().movesMade.size, 1)
    }

    @Test
    fun `update movements and check enemy`() {
        assertTrue(game.existPieceOn(Position(3, 1), game.whoIsMoving()))
        game.updateMovement(Bishop(Position(3, 1), Color.WHITE), Position(5, 3), game.whoIsMoving())
        assertTrue(game.existPieceOn(Position(5, 3), game.whoIsMoving()))
        game.updateTurn()
        assertTrue(game.existPieceOn(Position(5, 3), game.enemyOf(game.whoIsMoving())))
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
        game.updateMovement(Pawn(Position(5, 7), Color.BLACK), Position(5, 6), game.enemyOf(game.whoIsMoving()))
        val firstKingMoves = King(Position(5, 8), Color.BLACK).getPossibleMovements(game.enemyOf(game.whoIsMoving()), game)
        assertEquals(firstKingMoves.size, 1)
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
        game.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6), playerOne)
        assertTrue(game.existPieceOn(Position(4, 6), playerOne))
        game.updateTurn()
        assertTrue(game.isKingCheckedOf(game.whoIsMoving(), game.enemyOf(game.whoIsMoving()), game))
    }

    @Test
    fun `is King dead`() {
        game.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6), playerOne)
        assertTrue(game.existPieceOn(Position(4, 6), playerOne))
        game.updateTurn()
        val isChecked = game.isKingCheckedOf(game.whoIsMoving(), game.enemyOf(game.whoIsMoving()), game)
        val isDead = game.hasNoOwnMovements(game.whoIsMoving(), game.enemyOf(game.whoIsMoving()))
        assertTrue(isChecked && isDead)
    }

    @Test
    fun `is King is checked but with moves`() {
        game.updateMovement(Knight(Position(2, 1), Color.WHITE), Position(4, 6), playerOne)
        game.updateTurn()
        game.updateMovement(Knight(Position(5, 7), Color.BLACK), Position(5, 6), playerTwo)

        val isChecked = game.isKingCheckedOf(game.whoIsMoving(), game.enemyOf(game.whoIsMoving()), game)
        assertTrue(isChecked)
        val isDead = game.hasNoOwnMovements(game.whoIsMoving(), game.enemyOf(game.whoIsMoving()))
        assertFalse(isChecked && isDead)
    }
}
