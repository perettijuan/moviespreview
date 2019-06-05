package com.jpp.mp.screens.main.movies.fragments

import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.ext.getViewModel
import com.jpp.mp.screens.main.movies.MoviesFragment
import com.jpp.mp.screens.main.movies.MoviesFragmentViewModel
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import java.util.concurrent.Executor
import javax.inject.Inject

class TopRatedMoviesFragment : MoviesFragment() {

    override fun getViewModelInstance(viewModelFactory: ViewModelProvider.Factory) = getViewModel<TopRatedMoviesFragmentViewModel>(viewModelFactory)

    class TopRatedMoviesFragmentViewModel @Inject constructor(getMoviesUseCase: GetMoviesUseCase,
                                                              configMovieUseCase: ConfigMovieUseCase,
                                                              networkExecutor: Executor)
        : MoviesFragmentViewModel(getMoviesUseCase, configMovieUseCase, networkExecutor) {
        override val movieSection: MovieSection = MovieSection.TopRated
    }
}