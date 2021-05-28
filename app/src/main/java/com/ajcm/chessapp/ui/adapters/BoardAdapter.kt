package com.ajcm.chessapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ajcm.chessapp.R
import com.ajcm.design.ViewHolder
import com.ajcm.chessapp.extensions.getImage
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.pieces.Piece
import com.ajcm.domain.players.Player

class BoardAdapter(private var positionList: List<Position>, private val game: Game, private val onClickListener: (Piece, Player) -> Unit, private val onMoveClickListener: (Position, Player) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    var possiblesMoves: List<Position> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_board,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPosition = positionList[holder.bindingAdapterPosition]

        with(holder.itemView) {
            val bg = findViewById<View>(R.id.bgItem)
            val imgPiece = findViewById<ImageView>(R.id.imgPiece)
            val possibleView = findViewById<View>(R.id.bgPossibleMove)

            when {
                game.existPieceOn(currentPosition, game.playerOne) -> {
                    game.getChessPieceFrom(game.playerOne, currentPosition)?.let {
                        imgPiece.setImageResource(it.getImage())
                    }
                }
                game.existPieceOn(currentPosition, game.playerTwo) -> {
                    game.getChessPieceFrom(game.playerTwo, currentPosition)?.let {
                        imgPiece.setImageResource(it.getImage())
                    }
                }
                else -> {
                    imgPiece.setImageResource(0)
                }
            }

            if (possiblesMoves.contains(currentPosition)) {
                possibleView.visibility = View.VISIBLE
            } else {
                possibleView.visibility = View.GONE
            }

            if ((currentPosition.first + currentPosition.second) % 2 == 0) {
                bg.setBackgroundResource(R.drawable.white_board_piece)
            } else {
                bg.setBackgroundResource(R.drawable.black_board_piece)
            }

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
    }

    override fun getItemCount(): Int = positionList.size
}
