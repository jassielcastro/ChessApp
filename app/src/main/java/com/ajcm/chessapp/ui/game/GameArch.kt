package com.ajcm.chessapp.ui.game

import com.ajcm.design.archi.ActionState
import com.ajcm.design.archi.UiState
import com.ajcm.domain.board.Position
import com.ajcm.domain.game.Game
import com.ajcm.domain.players.Player

sealed class GameAction : ActionState {
    object Init : GameAction()
    object Reset : GameAction()
    object UpdateTurn : GameAction()
}

sealed class GameState : UiState {
    object SetUpViews : GameState()
    data class CreateGame(val game: Game) : GameState()
    data class ShowPossibleMoves(val moves: List<Position>) : GameState()
    object InvalidMove : GameState()
    object KingChecked : GameState()
    object Checkmate : GameState()
    object MoveFinished : GameState()
    data class ShowNewTurn(val playerMoving: Player) : GameState()
}
