package com.jpp.moviespreview.screens.main.movies.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.jpp.moviespreview.screens.main.movies.MoviesFragment

class UpcomingMoviesFragment : MoviesFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("JPPLogging", "UpcomingMoviesFragment")
    }
}