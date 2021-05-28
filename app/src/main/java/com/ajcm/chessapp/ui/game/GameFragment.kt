package com.ajcm.chessapp.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ajcm.chessapp.R
import com.ajcm.chessapp.databinding.GameFragmentBinding
import com.ajcm.chessapp.ui.adapters.BoardAdapter
import com.ajcm.design.SpanningGridLayoutManager
import com.ajcm.design.archi.BaseFragment
import com.ajcm.domain.board.Board
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player
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
            GameState.MoveFinished -> viewModel.dispatch(GameAction.UpdateTurn)
            is GameState.ShowNewTurn -> showNewTurn(state.playerMoving)
        }
    }

    private fun showNewTurn(playerMoving: Player) {
        println("GameFragment.showNewTurn --> $playerMoving")
    }

    private fun showCheckmate() {
        println("GameFragment.showCheckmate")
    }

    private fun showKingChecked() {
        println("GameFragment.showKingChecked")
    }

    private fun showInvalidMove() {
        println("GameFragment.showInvalidMove")
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
            game.board.positions,
            game,
            viewModel.clickPositionListener,
            viewModel.movedClickListener
        ).also { boardAdapter = it }
    }

}
