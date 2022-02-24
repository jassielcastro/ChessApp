package com.ajcm.chessapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ajcm.chess.Board
import com.ajcm.chess.Piece
import com.ajcm.chess.Player
import com.ajcm.chess.board.Position
import com.ajcm.chessapp.R
import com.ajcm.chessapp.extensions.getImage
import com.ajcm.design.ViewHolder

class BoardAdapter(
    private val board: Board,
    private val onClickListener: (Piece, Player) -> Unit,
    private val onMoveClickListener: (Position, Player) -> Unit,
) : RecyclerView.Adapter<ViewHolder>() {

    var possiblesMoves: List<Position> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_board, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPosition = board.positions[holder.bindingAdapterPosition]

        with(holder.itemView) {
            val bg = findViewById<View>(R.id.bgItem)
            val imgPiece = findViewById<ImageView>(R.id.imgPiece)
            val possibleView = findViewById<View>(R.id.bgPossibleMove)

            val pieceOnBoard = game.getPiecesOnBord(currentPosition)

            pieceOnBoard?.let {
                imgPiece.setImageResource(it.getImage())
            } ?: imgPiece.setImageResource(0)

            checkPossibleMoves(currentPosition, possibleView, pieceOnBoard)
            addClickListener(imgPiece, currentPosition)

            if ((currentPosition.x + currentPosition.y) % 2 == 0) {
                bg.setBackgroundResource(R.drawable.white_board_piece)
            } else {
                bg.setBackgroundResource(R.drawable.black_board_piece)
            }

        }
    }

    private fun checkPossibleMoves(currentPosition: Position, possibleView: View, pieceOnBoard: Piece?) {
        if (possiblesMoves.contains(currentPosition)) {
            possibleView.visibility = View.VISIBLE
            possibleView.background = pieceOnBoard?.let {
                ContextCompat.getDrawable(possibleView.context, R.drawable.possible_move_checked).also {
                    possibleView.alpha = 0.6f
                }
            } ?: ContextCompat.getDrawable(possibleView.context, R.drawable.possible_move_selector).also {
                possibleView.alpha = 1f
            }
        } else {
            possibleView.visibility = View.GONE
        }
    }

    private fun addClickListener(imgPiece: ImageView, currentPosition: Position) {
        imgPiece.setOnClickListener {
            with(game.whoIsMoving()) {
                game.getChessPieceFrom(this, currentPosition)?.let {
                    onClickListener(it, this)
                } ?: if (possiblesMoves.isNotEmpty() && possiblesMoves.contains(currentPosition)) {
                    onMoveClickListener(currentPosition, this)
                }
            }
        }
    }

    override fun getItemCount(): Int = board.positions.size
}
