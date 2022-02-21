package com.ajcm.chessapp.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.ajcm.chess.board.Color
import com.ajcm.chess.board.Player
import com.ajcm.chess.board.Board
import com.ajcm.chess.board.Position
import com.ajcm.chess.piece.Pawn
import com.ajcm.chess.piece.PawnTransform
import com.ajcm.chess.game.Game
import com.ajcm.chessapp.R
import com.ajcm.chessapp.databinding.GameFragmentBinding
import com.ajcm.chessapp.ui.adapters.BoardAdapter
import com.ajcm.design.SpanningGridLayoutManager
import com.ajcm.design.archi.BaseFragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : BaseFragment<GameState, GameAction, GameViewModel>(R.layout.game_fragment) {

    override val viewModel: GameViewModel by viewModel()

    private lateinit var binding: GameFragmentBinding
    private lateinit var boardAdapter: BoardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dispatch(GameAction.Init)
    }

    override fun render(state: GameState) {
        when(state) {
            GameState.SetUpViews -> setUpViews()
            is GameState.CreateGame -> setAdapter(state.game)
            is GameState.ShowPossibleMoves -> showPossibleMoves(state.moves)
            GameState.InvalidMove -> showInvalidMove()
            GameState.KingChecked -> showKingChecked()
            GameState.Checkmate -> showCheckmate()
            GameState.MoveFinished -> viewModel.dispatch(GameAction.CheckKingStatus)
            GameState.ShouldUpdateTurn -> viewModel.dispatch(GameAction.UpdateTurn)
            is GameState.ConvertPawnPiece -> showListOfPossibleChanges(state.pawn)
            GameState.UpdateNewPieces -> boardAdapter.notifyDataSetChanged()
            is GameState.ShowNewTurn -> showNewTurn(state.playerMoving)
        }
    }

    private fun showListOfPossibleChanges(pawn: Pawn) {
        showAlert("Pawn ready to transform!") {
            viewModel.dispatch(GameAction.ChangePawnPieceFor(PawnTransform.QUEEN, pawn))
        }
    }

    private fun showNewTurn(playerMoving: Player) = with(binding) {
        if (playerMoving.color == Color.WHITE) {
            txtPlayerWhite.text = getText(R.string.title_your_turn)
            txtPlayerBlack.text = getText(R.string.title_waiting)
            txtPlayerWhite.background = ContextCompat.getDrawable(requireContext(), R.drawable.turn_on_background)
            txtPlayerBlack.background = ContextCompat.getDrawable(requireContext(), R.drawable.turn_off_background)
        } else {
            txtPlayerWhite.text = getText(R.string.title_waiting)
            txtPlayerBlack.text = getText(R.string.title_your_turn)
            txtPlayerWhite.background = ContextCompat.getDrawable(requireContext(), R.drawable.turn_off_background)
            txtPlayerBlack.background = ContextCompat.getDrawable(requireContext(), R.drawable.turn_on_background)
        }
    }

    private fun showCheckmate() {
        showAlert("Game finished!") {
            viewModel.dispatch(GameAction.Reset)
        }
    }

    private fun showKingChecked() {
        showAlert("King checked!")
    }

    private fun showInvalidMove() {
        showAlert("Invalid movement...")
    }

    private fun showAlert(title: String, completion: () -> Unit = {}) {
        val snack = Snackbar.make(binding.rootGameView, title, Snackbar.LENGTH_SHORT)
        snack.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                completion()
            }
        })
        snack.show()
    }

    private fun showPossibleMoves(moves: List<Position>) {
        boardAdapter.possiblesMoves = moves
    }

    private fun setUpViews() = with(binding.gridBoard) {
        post {
            layoutParams.apply {
                height = measuredWidth
            }

            layoutManager = SpanningGridLayoutManager(context,
                Board.CELL_COUNT,
                GridLayoutManager.HORIZONTAL,
                false)

            viewModel.dispatch(GameAction.Reset)
        }
    }

    private fun setAdapter(game: Game) = with(binding.gridBoard) {
        adapter = BoardAdapter(
            game,
            viewModel.clickPositionListener,
            viewModel.movedClickListener
        ).also { boardAdapter = it }
    }

}
