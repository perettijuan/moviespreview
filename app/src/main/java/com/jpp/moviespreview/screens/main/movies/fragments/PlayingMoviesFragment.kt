package com.jpp.moviespreview.screens.main.movies.fragments

import com.jpp.moviespreview.screens.main.movies.MoviesFragment
import com.jpp.moviespreview.screens.main.movies.UiMovieSection

class PlayingMoviesFragment : MoviesFragment() {

    override fun getMoviesSection(): UiMovieSection = UiMovieSection.Playing


    override fun getNavDirectionsForMovieDetails(movieId: String) =
        PlayingMoviesFragmentDirections.actionPlayingMoviesFragmentToMovieDetailsFragment(movieId)

}