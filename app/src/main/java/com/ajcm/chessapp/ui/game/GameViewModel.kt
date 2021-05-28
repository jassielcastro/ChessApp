package com.ajcm.chessapp.ui.game

import androidx.lifecycle.LiveData
import com.ajcm.chessapp.game.GameSourceImpl
import com.ajcm.design.archi.ScopedViewModel
import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.random.Random

class GameViewModel(uiDispatcher: CoroutineDispatcher) : ScopedViewModel<GameState, GameAction>(uiDispatcher) {

    override val model: LiveData<GameState>
        get() = mModel

    private lateinit var playerOne: Player
    private lateinit var playerTwo: Player
    private lateinit var game: Game
    private lateinit var gameSource: GameSourceImpl

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
            GameAction.UpdateTurn -> updateTurn()
        }
    }

    private fun updateTurn() {
        gameSource.updateTurn()
        consume(GameState.ShowNewTurn(gameSource.whoIsMoving(game)))
    }

    private fun resetGame() {
        val randomTurn = Random.nextInt(0, 1)
        playerOne = Player(if (randomTurn == 1) Color.WHITE else Color.BLACK)
        playerTwo = Player(if (randomTurn == 1) Color.BLACK else Color.WHITE)

        game = Game(playerOne, playerTwo, Board())
        gameSource = GameSourceImpl(game)

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
            if (isKingChecked(gameSource.getEnemyOf(player, game))) {
                for (position in moves) {
                    if (!gameSource.isValidMadeFakeMovement(piece.position, position, gameSource.getEnemyOf(player, game), player, game)) {
                        validMoves.add(position)
                    }
                }
            } else {
                validMoves.addAll(moves)
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
            if (!gameSource.isKingEnemy(newPosition, gameSource.getEnemyOf(player, game))) {
                gameSource.updateMovement(it, newPosition, player, game)
                clearPossibleMoves()
                if (isKingChecked(player)) {
                    consume(GameState.KingChecked)
                    if (gameSource.hasNoOwnMovements(player, gameSource.getEnemyOf(player, game), game)) {
                        consume(GameState.Checkmate)
                    }
                }
                consume(GameState.MoveFinished)
            } else {
                consume(GameState.InvalidMove)
            }
        }
    }

    private fun isKingChecked(player: Player): Boolean {
        return gameSource.isKingCheckedOf(player, gameSource.getEnemyOf(player, game), game)
    }
}