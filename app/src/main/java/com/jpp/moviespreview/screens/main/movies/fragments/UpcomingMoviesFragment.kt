package com.jpp.moviespreview.screens.main.movies.fragments

import com.jpp.moviespreview.screens.main.movies.MoviesFragment
import com.jpp.moviespreview.screens.main.movies.UiMovieSection

class UpcomingMoviesFragment : MoviesFragment() {

    override fun getMoviesSection(): UiMovieSection = UiMovieSection.Upcoming

    override fun getNavDirectionsForMovieDetails(movieId: String, movieImageUrl: String) =
        UpcomingMoviesFragmentDirections.actionUpcomingMoviesFragmentToMovieDetailsFragment(movieId, movieImageUrl)
}