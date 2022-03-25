package com.ajcm.chessapp.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ajcm.chess.board.Board
import com.ajcm.chess.board.PlayerStatus
import com.ajcm.chess.piece.PawnTransform
import com.ajcm.chessapp.R
import com.ajcm.chessapp.databinding.GameFragmentBinding
import com.ajcm.chessapp.ui.adapters.BoardAdapter
import com.ajcm.design.SpanningGridLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : Fragment(R.layout.game_fragment) {

    private val viewModel: GameViewModel by viewModel()

    private lateinit var binding: GameFragmentBinding
    private lateinit var boardAdapter: BoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()

        setAdapter(viewModel.board)

        lifecycleScope.launchWhenCreated {
            addObservers()
        }
    }

    private fun addObservers() = with(lifecycleScope) {
        launch {
            viewModel.whiteAvailablePieces.collect {
                boardAdapter.whiteAvailablePieces = it
            }
        }

        launch {
            viewModel.blackAvailablePieces.collect {
                boardAdapter.blackAvailablePieces = it
            }
        }

        launch {
            viewModel.whitePlayerStatus.collect {
                isPlayerMoving(it, binding.txtPlayerWhite)
            }
        }

        launch {
            viewModel.blackPlayerStatus.collect {
                isPlayerMoving(it, binding.txtPlayerBlack)
            }
        }

        launch {
            viewModel.whiteDeadPieces.collect {
                println("GameFragment.addObservers ---> firstPlayerDeadPieces: $it")
            }
        }

        launch {
            viewModel.blackDeadPieces.collect {
                println("GameFragment.addObservers ---> secondPlayerDeadPieces: $it")
            }
        }

        launch {
            viewModel.whiteKingStatus.collect {
                println("GameFragment.addObservers ---> whiteKingStatus: $it")
            }
        }

        launch {
            viewModel.blackKingStatus.collect {
                println("GameFragment.addObservers ---> blackKingStatus: $it")
            }
        }

        launch {
            viewModel.pawnToEvolve.collect {
                it?.let {
                    viewModel.evolvePawn(it, PawnTransform.QUEEN)
                }
            }
        }

    }

    private fun isPlayerMoving(status: PlayerStatus, textView: TextView) {
        if (status == PlayerStatus.MOVING) {
            textView.text = getText(R.string.title_your_turn)
            textView.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.turn_on_background)
        } else {
            textView.text = getText(R.string.title_waiting)
            textView.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.turn_off_background)
        }
    }

    private fun setUpViews() = with(binding.gridBoard) {
        post {
            layoutParams.apply {
                height = measuredWidth
            }

            layoutManager = SpanningGridLayoutManager(
                context,
                Board.CELL_COUNT,
                GridLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun setAdapter(board: Board) = with(binding.gridBoard) {
        adapter = BoardAdapter(
            board = board,
            viewModel.clickPositionListener,
            viewModel.movedClickListener
        ).also { boardAdapter = it }
    }

}
