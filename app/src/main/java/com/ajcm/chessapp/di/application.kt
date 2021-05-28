package com.ajcm.chessapp.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

fun Application.init() {
    startKoin {
        androidLogger(Level.ERROR)
        androidContext(this@init)
        modules(listOf(appModule, fragments))
    }
}