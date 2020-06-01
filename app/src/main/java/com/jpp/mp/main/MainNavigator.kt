package com.jpp.mp.main

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.jpp.mp.R
import com.jpp.mp.main.movies.MovieListNavigator
import com.jpp.mpmoviedetails.NavigationMovieDetails
/**
 * Provides navigation to the main module and also to each individual
 * feature module.
 */
class MainNavigator : MovieListNavigator {

    private var navController: NavController? = null


    override fun navigateToMovieDetails(
            movieId: String,
            movieImageUrl: String,
            movieTitle: String
    ) {
        navController?.navigate(R.id.movie_details_nav,
                NavigationMovieDetails.navArgs(
                        movieId,
                        movieImageUrl,
                        movieTitle
                ),
                buildAnimationNavOptions()
        )

    }

    fun bind(newNavController: NavController) {
        navController = newNavController
    }

    fun unBind() {
        navController = null
    }

    private fun buildAnimationNavOptions() = NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_enter_slide_right)
            .setExitAnim(R.anim.fragment_exit_slide_right)
            .setPopEnterAnim(R.anim.fragment_enter_slide_left)
            .setPopExitAnim(R.anim.fragment_exit_slide_left)
            .build()
}