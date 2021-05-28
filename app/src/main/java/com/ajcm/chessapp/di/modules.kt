package com.ajcm.chessapp.di

import com.ajcm.chessapp.ui.game.GameViewModel
import com.ajcm.chessapp.ui.splash.SplashViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<CoroutineDispatcher> { Dispatchers.Main }
}

val fragments = module {
    viewModel { SplashViewModel( get()) }
    viewModel { GameViewModel( get()) }
}
