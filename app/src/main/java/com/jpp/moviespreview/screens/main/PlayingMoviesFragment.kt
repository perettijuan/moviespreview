package com.jpp.moviespreview.screens.main

import android.os.Bundle
import android.util.Log
import android.view.View

class PlayingMoviesFragment : MoviesFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("JPPLogging", "Playing")
    }
}