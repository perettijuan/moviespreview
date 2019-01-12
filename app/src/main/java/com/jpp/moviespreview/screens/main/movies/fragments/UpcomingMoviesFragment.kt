package com.jpp.moviespreview.screens.main.movies.fragments

import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.ext.getViewModel
import com.jpp.moviespreview.screens.main.movies.MoviesFragment
import com.jpp.moviespreview.screens.main.movies.MoviesFragmentViewModel
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.repository.movies.MovieListRepository
import javax.inject.Inject

class UpcomingMoviesFragment : MoviesFragment() {

    override fun getNavDirectionsForMovieDetails(movieId: String, movieImageUrl: String) =
        UpcomingMoviesFragmentDirections.actionUpcomingMoviesFragmentToMovieDetailsFragment(movieId, movieImageUrl)


    override fun getViewModelInstance(viewModelFactory: ViewModelProvider.Factory) = getViewModel<UpcomingMoviesFragmentViewModel>(viewModelFactory)

    class UpcomingMoviesFragmentViewModel @Inject constructor(movieListRepository: MovieListRepository)
        : MoviesFragmentViewModel(movieListRepository) {
        override val movieSection: MovieSection = MovieSection.Upcoming
    }
}