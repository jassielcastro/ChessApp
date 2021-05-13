package com.ajcm.design

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.ajcm.domain.board.Color
import com.ajcm.domain.board.Position

class BoardPieceView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    var position: Position = Position(0, 0)
    var color: Color = Color.WHITE
        set(value) {
            setImageResource(if (color == Color.WHITE) R.drawable.white_board_piece else R.drawable.black_board_piece)
            field = value
        }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

}
