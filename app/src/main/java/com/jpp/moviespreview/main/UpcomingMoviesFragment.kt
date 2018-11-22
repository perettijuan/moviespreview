package com.jpp.moviespreview.main

import android.os.Bundle
import android.util.Log
import android.view.View

class UpcomingMoviesFragment : MoviesFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("JPPLogging", "UpcomingMoviesFragment")
    }
}