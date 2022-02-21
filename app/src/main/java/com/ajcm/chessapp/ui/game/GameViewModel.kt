package com.ajcm.chessapp.ui.game

import androidx.lifecycle.LiveData
import com.ajcm.chess.game.Game
import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Board
import com.ajcm.chess.board.Position
import com.ajcm.chess.piece.*
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

    private fun changePawnPiece(newPawn: PawnTransform, currentPawn: Pawn) {
        game.convert(currentPawn, newPawn)

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
            val moves = piece.getPossibleMoves(player, game)
            if (moves.isNotEmpty()) {
                lastPieceSelected = piece
                consume(GameState.ShowPossibleMoves(moves))
                return
            }
        }
        clearPossibleMoves()
    }

    private fun makeMovement(newPosition: Position, player: Player) {
        lastPieceSelected?.let {
            if (!game.isKingEnemyOn(newPosition, game.enemyOf(player))) {
                game.updateMovement(it, newPosition, player)
                clearPossibleMoves()
                if (it.canConvertPiece()) {
                    consume(GameState.ConvertPawnPiece(it as Pawn))
                    return
                }
                consume(GameState.MoveFinished)
            } else {
                consume(GameState.InvalidMove)
            }
        }
    }

    private fun checkKingStatus(player: Player) {
        if (game.isKingCheckedOf(player, game.enemyOf(player))) {
            consume(GameState.KingChecked)
            if (game.hasNoOwnMovements(player, game.enemyOf(player))) {
                consume(GameState.Checkmate)
            }
        }
        consume(GameState.ShouldUpdateTurn)
    }

}
