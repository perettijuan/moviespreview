package com.jpp.mp.main.movies

import androidx.transition.Transition
import com.jpp.mp.R
import com.jpp.mp.main.movies.transitions.MovieListErrorTransition
import com.jpp.mp.main.movies.transitions.MovieListInTransition
import com.jpp.mp.main.movies.transitions.MovieListLoading

/**
 * Represents the animations that take place in the movie list section.
 */
data class MovieListAnimations(
    val rootTransition: Transition? = null,
    val itemAnimationId: Int = -1
) {
    fun loadingToMovieList(): MovieListAnimations =
        MovieListAnimations(
            rootTransition = MovieListInTransition(),
            itemAnimationId = R.anim.slide_from_bottom_list_item
        )

    fun anyToError(): MovieListAnimations =
        MovieListAnimations(
            rootTransition = MovieListErrorTransition()
        )

    fun anyToLoading(): MovieListAnimations =
        MovieListAnimations(
            rootTransition = MovieListLoading()
        )

    companion object {
        fun noAnimations() = MovieListAnimations()
    }
}
