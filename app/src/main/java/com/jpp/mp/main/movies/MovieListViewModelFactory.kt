package com.jpp.mp.main.movies

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.ConfigureMovieImagesPathUseCase
import com.jpp.mpdomain.usecase.GetMoviePageUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [MovieListViewModel] instances
 * with the dependencies provided by Dagger.
 * Note that Dagger inject's this object and this object takes care of creating
 * the [MovieListViewModel] instance needed.
 */
class MovieListViewModelFactory @Inject constructor(
    private val getMoviePageUseCase: GetMoviePageUseCase,
    private val configureMovieImagesPathUseCase: ConfigureMovieImagesPathUseCase,
    private val navigator: MovieListNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<MovieListViewModel> {

    override fun create(handle: SavedStateHandle): MovieListViewModel {
        return MovieListViewModel(
            getMoviePageUseCase,
            configureMovieImagesPathUseCase,
            navigator,
            ioDispatcher,
            handle
        )
    }
}