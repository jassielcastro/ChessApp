package com.ajcm.chessapp

import android.app.Application
import com.ajcm.chessapp.di.init

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

}