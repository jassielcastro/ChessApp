package com.ajcm.chessapp.ui.game

import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position
import com.ajcm.chess.domain.piece.Piece
import com.ajcm.design.archi.ActionState
import com.ajcm.design.archi.UiState

sealed class GameAction : ActionState {
    object Init : GameAction()
    object Reset : GameAction()
    object UpdateTurn : GameAction()
    object CheckKingStatus : GameAction()
    data class ChangePawnPieceFor(val newPiece: PawnTransform, val currentPiece: Piece): GameAction()
}

sealed class GameState : UiState {
    object SetUpViews : GameState()
    data class CreateGame(val game: com.ajcm.chess.data.Game) : GameState()
    data class ShowPossibleMoves(val moves: List<Position>) : GameState()
    object InvalidMove : GameState()
    object KingChecked : GameState()
    object Checkmate : GameState()
    data class ConvertPawnPiece(val pawn: Piece): GameState()
    object UpdateNewPieces : GameState()
    object MoveFinished : GameState()
    object ShouldUpdateTurn : GameState()
    data class ShowNewTurn(val playerMoving: Player) : GameState()
}

enum class PawnTransform {
    BISHOP, KNIGHT, QUEEN, ROOK
}
