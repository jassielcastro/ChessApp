package com.ajcm.chessapp.ui.game

import androidx.lifecycle.LiveData
import com.ajcm.chess.data.Game
import com.ajcm.chess.domain.Color
import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Board
import com.ajcm.chess.domain.board.Position
import com.ajcm.chess.domain.piece.*
import com.ajcm.chess.game.GameSource
import com.ajcm.design.archi.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.random.Random

class GameViewModel(uiDispatcher: CoroutineDispatcher) : ScopedViewModel<GameState, GameAction>(uiDispatcher) {

    override val model: LiveData<GameState>
        get() = mModel

    private lateinit var playerOne: Player
    private lateinit var playerTwo: Player
    private lateinit var game: Game

    private var lastPieceSelected: Piece? = null

    val clickPositionListener: (Piece, Player) -> Unit = (::getMovementsOf)
    val movedClickListener: (Position, Player) -> Unit = (::makeMovement)

    init {
        initScope()
    }

    override fun dispatch(actionState: GameAction) {
        when(actionState) {
            GameAction.Init -> consume(GameState.SetUpViews)
            GameAction.Reset -> resetGame()
            GameAction.CheckKingStatus -> checkKingStatus(game.whoIsMoving())
            GameAction.UpdateTurn -> updateTurn()
            is GameAction.ChangePawnPieceFor -> changePawnPiece(actionState.newPiece, actionState.currentPiece)
        }
    }

    private fun changePawnPiece(newPiece: PawnTransform, currentPiece: Piece) {
        val piece = when (newPiece) {
            PawnTransform.BISHOP -> Bishop(currentPiece.position, currentPiece.color)
            PawnTransform.KNIGHT -> Knight(currentPiece.position, currentPiece.color)
            PawnTransform.QUEEN -> Queen(currentPiece.position, currentPiece.color)
            PawnTransform.ROOK -> Rook(currentPiece.position, currentPiece.color)
        }

        with(game.whoIsMoving().availablePieces) {
            remove(currentPiece)
            add(piece)
        }

        consume(GameState.UpdateNewPieces)
        consume(GameState.MoveFinished)
    }

    private fun updateTurn() {
        game.updateTurn()
        consume(GameState.ShowNewTurn(game.whoIsMoving()))
    }

    private fun resetGame() {
        val randomTurn = Random.nextInt(0, 1)
        playerOne = Player(if (randomTurn == 1) Color.WHITE else Color.BLACK)
        playerTwo = Player(if (randomTurn == 1) Color.BLACK else Color.WHITE)

        game = GameSource(playerOne, playerTwo, Board())

        consume(GameState.CreateGame(game))
    }

    private fun clearPossibleMoves() {
        lastPieceSelected = null
        consume(GameState.ShowPossibleMoves(emptyList()))
    }

    private fun getMovementsOf(piece: Piece, player: Player) {
        if (lastPieceSelected != piece) {
            val moves = piece.getPossibleMovements(player, game)
            val validMoves = mutableListOf<Position>()
            for (position in moves) {
                if (!game.isValidMadeFakeMovement(piece.position, position, game.enemyOf(player))) {
                    validMoves.add(position)
                }
            }
            if (validMoves.isNotEmpty()) {
                lastPieceSelected = piece
                consume(GameState.ShowPossibleMoves(validMoves))
                return
            }
        }
        clearPossibleMoves()
    }

    private fun makeMovement(newPosition: Position, player: Player) {
        lastPieceSelected?.let {
            if (!game.isKingEnemy(newPosition, game.enemyOf(player))) {
                if (it is King && newPosition.x == 7) {
                    // Castling movement
                    val y = if (player.color == Color.WHITE) 1 else 8
                    game.getChessPieceFrom(player, Position(8, y))?.let { rook ->
                        game.updateMovement(rook, Position(6, y), player)
                    }
                }
                game.updateMovement(it, newPosition, player)
                clearPossibleMoves()
                if (it.canConvertPiece()) {
                    consume(GameState.ConvertPawnPiece(it))
                    return
                }
                consume(GameState.MoveFinished)
            } else {
                consume(GameState.InvalidMove)
            }
        }
    }

    private fun checkKingStatus(player: Player) {
        if (isKingChecked(player)) {
            consume(GameState.KingChecked)
            if (game.hasNoOwnMovements(player, game.enemyOf(player))) {
                consume(GameState.Checkmate)
            }
        }
        consume(GameState.ShouldUpdateTurn)
    }

    private fun isKingChecked(player: Player): Boolean {
        return game.isKingCheckedOf(player, game.enemyOf(player))
    }
}