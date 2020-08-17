package com.jpp.mp.main.movies.transitions

import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.jpp.mp.R
/**
 * Transition that goes from any given state to the error state.
 */
class MovieListErrorTransition : TransitionSet() {

    init {
        val fadeOut = Fade(Fade.MODE_OUT).apply {
            addTarget(R.id.moviesLoadingView)
            addTarget(R.id.movieList)
            duration = 300
        }
        val fadeIn = Fade(Fade.MODE_IN).apply {
            addTarget(R.id.movieListErrorView)
            duration = 200
            startDelay = 300
        }
        addTransition(fadeOut)
        addTransition(fadeIn)
    }
}
