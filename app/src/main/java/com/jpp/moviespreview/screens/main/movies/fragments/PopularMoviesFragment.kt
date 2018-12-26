package com.jpp.moviespreview.screens.main.movies.fragments

import com.jpp.moviespreview.screens.main.movies.MoviesFragment
import com.jpp.moviespreview.screens.main.movies.UiMovieSection

class PopularMoviesFragment : MoviesFragment() {

    override fun getMoviesSection(): UiMovieSection = UiMovieSection.Popular

    override fun getNavDirectionsForMovieDetails(movieId: String) =
        PopularMoviesFragmentDirections.actionPopularMoviesFragmentToMovieDetailsFragment(movieId)
}