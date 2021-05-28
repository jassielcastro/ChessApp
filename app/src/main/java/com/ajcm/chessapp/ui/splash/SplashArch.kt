package com.ajcm.chessapp.ui.splash

import com.ajcm.design.archi.ActionState
import com.ajcm.design.archi.UiState

sealed class SplashAction : ActionState {
    object Init : SplashAction()
    object GetNextScreen : SplashAction()
}

sealed class SplashState : UiState {
    object StartAnimation : SplashState()
    data class MoveNextScreen(val screenId: Int) : SplashState()
}
