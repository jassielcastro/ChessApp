package com.ajcm.chessapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ajcm.chessapp.databinding.ActivityMainBinding
import com.ajcm.chessapp.game.GameSourceImpl
import com.ajcm.chessapp.ui.adapters.BoardAdapter
import com.ajcm.design.SpanningGridLayoutManager
import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var playerOne: Player
    private lateinit var playerTwo: Player
    private lateinit var game: Game
    private lateinit var gameSource: GameSourceImpl

    private lateinit var binding: ActivityMainBinding
    private lateinit var boardAdapter: BoardAdapter
    private var lastPieceSelected: Piece? = null

    private val clickPositionListener: (Piece, Player) -> Unit = (::getMovementsOf)
    private val movedClickListener: (Position, Player) -> Unit = (::makeMovement)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetGame()
        setUpViews()
    }

    private fun resetGame() {
        val randomTurn = Random.nextInt(0, 1)
        playerOne = Player(if (randomTurn == 1) Color.WHITE else Color.BLACK)
        playerTwo = Player(if (randomTurn == 1) Color.BLACK else Color.WHITE)

        game = Game(playerOne, playerTwo, Board())
        gameSource = GameSourceImpl(game)
    }

    private fun setUpViews() = with(binding.gridBoard) {
        post {
            layoutParams.apply {
                height = measuredWidth
            }

            layoutManager = SpanningGridLayoutManager(this@MainActivity,
                Board.CELL_COUNT,
                GridLayoutManager.HORIZONTAL,
                false)

            adapter = BoardAdapter(
                game.board.positions,
                game,
                clickPositionListener,
                movedClickListener
            ).also { boardAdapter = it }
        }
    }

    private fun clearPossibleMoves() {
        boardAdapter.possiblesMoves = emptyList()
        lastPieceSelected = null
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
                boardAdapter.possiblesMoves = validMoves
                lastPieceSelected = piece
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
                    Toast.makeText(this@MainActivity, "King checked", Toast.LENGTH_SHORT).show()
                    if (gameSource.hasNoOwnMovements(player, gameSource.getEnemyOf(player, game), game)) {
                        Toast.makeText(this@MainActivity, "Jake mate", Toast.LENGTH_SHORT).show()
                    }
                }
                gameSource.updateTurn()
            } else {
                Toast.makeText(this@MainActivity, "Invalid move - Cannot eat the king", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isKingChecked(player: Player): Boolean {
        return gameSource.isKingCheckedOf(player, gameSource.getEnemyOf(player, game), game)
    }

}
