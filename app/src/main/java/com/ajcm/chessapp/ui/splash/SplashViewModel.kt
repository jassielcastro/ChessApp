package com.ajcm.chessapp.ui.splash

import androidx.lifecycle.LiveData
import com.ajcm.chessapp.R
import com.ajcm.design.archi.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class SplashViewModel(uiDispatcher: CoroutineDispatcher) : ScopedViewModel<SplashState, SplashAction>(uiDispatcher) {

    override val model: LiveData<SplashState>
        get() = mModel

    init {
        initScope()
    }

    override fun dispatch(actionState: SplashAction) {
        when (actionState) {
            SplashAction.Init -> consume(SplashState.StartAnimation)
            SplashAction.GetNextScreen -> consume(SplashState.MoveNextScreen(R.id.action_splashFragment_to_gameFragment))
        }
    }
}
