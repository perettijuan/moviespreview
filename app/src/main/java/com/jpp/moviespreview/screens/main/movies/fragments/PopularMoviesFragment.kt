package com.jpp.moviespreview.screens.main.movies.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import com.jpp.moviespreview.screens.main.movies.MoviesFragment

class PopularMoviesFragment : MoviesFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("JPPLogging", "Popular")
    }
}