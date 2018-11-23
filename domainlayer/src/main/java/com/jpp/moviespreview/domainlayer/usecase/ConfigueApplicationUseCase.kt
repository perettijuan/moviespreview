package com.jpp.moviespreview.domainlayer.usecase

import android.os.Looper

class ConfigueApplicationUseCase {

    //TODO JPP idea -> puedo usar Kotlin Channels??
    fun execute(): ConfigureApplicationState {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw RuntimeException("Running on UI thread")
        }

        Thread.sleep(2000)
        return ConfigureApplicationState.Success
    }
}