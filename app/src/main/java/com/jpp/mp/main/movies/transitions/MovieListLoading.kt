package com.jpp.mp.main.movies.transitions

import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.jpp.mp.R

class MovieListLoading : TransitionSet() {

    init {
        val fadeOut = Fade(Fade.MODE_OUT).apply {
            addTarget(R.id.movieListErrorView)
            addTarget(R.id.movieList)
            duration = 300
        }
        val fadeIn = Fade(Fade.MODE_IN).apply {
            addTarget(R.id.moviesLoadingView)
            duration = 200
            startDelay = 300
        }
        addTransition(fadeOut)
        addTransition(fadeIn)
    }
}
