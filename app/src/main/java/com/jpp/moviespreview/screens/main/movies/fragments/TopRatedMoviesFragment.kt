package com.jpp.moviespreview.screens.main.movies.fragments

import com.jpp.moviespreview.screens.main.movies.MoviesFragment
import com.jpp.moviespreview.screens.main.movies.UiMovieSection

class TopRatedMoviesFragment : MoviesFragment() {

    override fun getMoviesSection(): UiMovieSection = UiMovieSection.TopRated

    override fun getNavDirectionsForMovieDetails(movieId: String) =
        TopRatedMoviesFragmentDirections.actionTopRatedMoviesFragmentToMovieDetailsFragment(movieId)
}