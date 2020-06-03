package com.jpp.mp.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.jpp.mp.R
import com.jpp.mp.main.movies.MovieListNavigator
import com.jpp.mpmoviedetails.MovieDetailsNavigator
import com.jpp.mpmoviedetails.NavigationMovieDetails

/**
 * Provides navigation to the main module and also to each individual
 * feature module.
 */
class MainNavigator : MovieListNavigator, MovieDetailsNavigator {

    private var navController: NavController? = null

    override fun navigateToMovieDetails(
        movieId: String,
        movieImageUrl: String,
        movieTitle: String
    ) {
        navController?.navigate(
            R.id.movie_details_nav,
            NavigationMovieDetails.navArgs(
                movieId,
                movieImageUrl,
                movieTitle
            ),
            buildAnimationNavOptions()
        )

    }

    override fun navigateToSearch() {
        navController?.navigate(
            object : NavDirections {
                override fun getArguments() = Bundle()
                override fun getActionId() = R.id.search_nav
            },
            buildAnimationNavOptions()
        )
    }

    override fun navigateToAboutSection() {
        navController?.navigate(
            R.id.about_nav,
            null,
            buildAnimationNavOptions()
        )
    }

    override fun navigateToUserAccount() {
        TODO("Not yet implemented")
    }

    override fun navigateToMovieCredits(movieId: Double, movieTitle: String) {
        TODO("Not yet implemented")
    }

    override fun navigateToRateMovie(movieId: Double, movieImageUrl: String, movieTitle: String) {
        TODO("Not yet implemented")
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