package com.ajcm.chessapp.ui.game

import androidx.lifecycle.ViewModel
import com.ajcm.chess.board.Board
import com.ajcm.chess.board.KingStatus
import com.ajcm.chess.board.PlayerStatus
import com.ajcm.chess.board.Position
import com.ajcm.chess.piece.PawnTransform
import com.ajcm.chess.piece.Pawn
import com.ajcm.chess.piece.Piece
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {

    var board: Board = Board()

    val whitePlayerStatus: StateFlow<PlayerStatus>
        get() = board.whitePlayerStatus
    val blackPlayerStatus: StateFlow<PlayerStatus>
        get() = board.blackPlayerStatus

    val whiteAvailablePieces: StateFlow<List<Piece>>
        get() = board.whiteAvailablePieces
    val blackAvailablePieces: StateFlow<List<Piece>>
        get() = board.blackAvailablePieces

    val whiteDeadPieces: StateFlow<List<Piece>>
        get() = board.whiteDeadPieces
    val blackDeadPieces: StateFlow<List<Piece>>
        get() = board.blackDeadPieces

    val whiteKingStatus: StateFlow<KingStatus>
        get() = board.whiteKingStatus
    val blackKingStatus: StateFlow<KingStatus>
        get() = board.blackKingStatus

    val pawnToEvolve: StateFlow<Pawn?>
        get() = board.pawnToEvolve

    private var lastPieceSelected: Piece? = null

    val clickPositionListener: (Piece) -> Unit = (::getMovementsOf)
    val movedClickListener: (Position) -> Unit = (::makeMovement)

    fun resetGame() {
        board = Board()
    }

    private fun getMovementsOf(piece: Piece) {
        lastPieceSelected = if (lastPieceSelected != piece) {
            piece
        } else {
            null
        }
    }

    private fun makeMovement(newPosition: Position) {
        lastPieceSelected?.let {
            it.updatePosition(newPosition)
            lastPieceSelected = null
        }
    }

    fun evolvePawn(pawnV2: Pawn, transform: PawnTransform) {
        board.transform(pawnV2, transform)
    }

}
