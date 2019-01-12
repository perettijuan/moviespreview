package com.jpp.moviespreview.screens.main.movies.fragments

import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.ext.getViewModel
import com.jpp.moviespreview.screens.main.movies.MoviesFragment
import com.jpp.moviespreview.screens.main.movies.MoviesFragmentViewModel
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.movies.MovieListRepository
import javax.inject.Inject

class PlayingMoviesFragment : MoviesFragment() {

    override fun getNavDirectionsForMovieDetails(movieId: String, movieImageUrl: String) =
        PlayingMoviesFragmentDirections.actionPlayingMoviesFragmentToMovieDetailsFragment(movieId, movieImageUrl)

    override fun getViewModelInstance(viewModelFactory: ViewModelProvider.Factory) = getViewModel<PlayingMoviesFragmentViewModel>(viewModelFactory)

    class PlayingMoviesFragmentViewModel @Inject constructor(movieListRepository: MovieListRepository)
        : MoviesFragmentViewModel(movieListRepository, MovieSection.Playing)

}