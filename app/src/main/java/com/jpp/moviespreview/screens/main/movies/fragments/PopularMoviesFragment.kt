package com.jpp.moviespreview.screens.main.movies.fragments

import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.ext.getViewModel
import com.jpp.moviespreview.screens.main.movies.MoviesFragment
import com.jpp.moviespreview.screens.main.movies.MoviesFragmentViewModel
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import java.util.concurrent.Executor
import javax.inject.Inject

class PopularMoviesFragment : MoviesFragment() {


    override fun getNavDirectionsForMovieDetails(movieId: String, movieImageUrl: String, movieTitle: String) =
        PopularMoviesFragmentDirections.actionPopularMoviesFragmentToMovieDetailsFragment(movieId, movieImageUrl, movieTitle)

    override fun getViewModelInstance(viewModelFactory: ViewModelProvider.Factory) = getViewModel<PopularMoviesFragmentViewModel>(viewModelFactory)

    class PopularMoviesFragmentViewModel @Inject constructor(getMoviesUseCase: GetMoviesUseCase,
                                                             configMovieUseCase: ConfigMovieUseCase,
                                                             networkExecutor: Executor)
        : MoviesFragmentViewModel(getMoviesUseCase, configMovieUseCase, networkExecutor) {
        override val movieSection: MovieSection = MovieSection.Popular
    }
}