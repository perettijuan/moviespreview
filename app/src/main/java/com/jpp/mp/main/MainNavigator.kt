package com.jpp.mp.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.jpp.mp.R
import com.jpp.mp.main.movies.MovieListNavigator
import com.jpp.mpcredits.CreditNavigator
import com.jpp.mpcredits.NavigationCredits
import com.jpp.mpmoviedetails.MovieDetailsFragmentDirections
import com.jpp.mpmoviedetails.MovieDetailsNavigator
import com.jpp.mpmoviedetails.NavigationMovieDetails
import com.jpp.mpmoviedetails.rates.RateMovieNavigator
import com.jpp.mpperson.NavigationPerson

/**
 * Provides navigation to the main module and also to each individual
 * feature module.
 */
class MainNavigator : MovieListNavigator,
    MovieDetailsNavigator,
    RateMovieNavigator,
    CreditNavigator {

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
        navController?.navigate(
            R.id.user_account_nav,
            null,
            buildAnimationNavOptions()
        )
    }

    override fun navigateToMovieCredits(movieId: Double, movieTitle: String) {
        navController?.navigate(
            R.id.credits_nav,
            NavigationCredits.navArgs(
                movieId,
                movieTitle
            ),
            buildAnimationNavOptions()
        )
    }

    override fun navigateToRateMovie(movieId: Double, movieImageUrl: String, movieTitle: String) {
        navController?.navigate(
            MovieDetailsFragmentDirections.rateMovie(
                movieId.toString(),
                movieImageUrl,
                movieTitle
            )
        )
    }

    override fun navigateToCreditDetail(
        personId: String,
        personImageUrl: String,
        personName: String
    ) {
        navController?.navigate(
            R.id.person_nav,
            NavigationPerson.navArgs(
                personId,
                personImageUrl,
                personName
            ),
            buildAnimationNavOptions()
        )
    }

    override fun navigateBack() {
        navController?.popBackStack()
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