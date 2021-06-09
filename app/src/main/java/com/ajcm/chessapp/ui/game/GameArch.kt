package com.ajcm.chessapp.ui.game

import com.ajcm.chess.domain.Player
import com.ajcm.chess.domain.board.Position
import com.ajcm.chess.domain.piece.Pawn
import com.ajcm.chess.domain.piece.PawnTransform
import com.ajcm.design.archi.ActionState
import com.ajcm.design.archi.UiState

sealed class GameAction : ActionState {
    object Init : GameAction()
    object Reset : GameAction()
    object UpdateTurn : GameAction()
    object CheckKingStatus : GameAction()
    data class ChangePawnPieceFor(val newPiece: PawnTransform, val currentPiece: Pawn): GameAction()
}

sealed class GameState : UiState {
    object SetUpViews : GameState()
    data class CreateGame(val game: com.ajcm.chess.data.Game) : GameState()
    data class ShowPossibleMoves(val moves: List<Position>) : GameState()
    object InvalidMove : GameState()
    object KingChecked : GameState()
    object Checkmate : GameState()
    data class ConvertPawnPiece(val pawn: Pawn): GameState()
    object UpdateNewPieces : GameState()
    object MoveFinished : GameState()
    object ShouldUpdateTurn : GameState()
    data class ShowNewTurn(val playerMoving: Player) : GameState()
}

