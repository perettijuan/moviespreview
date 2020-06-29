package com.jpp.mpmoviedetails

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetMovieDetailUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [MovieDetailsViewModel] instances
 * with the dependencies provided by Dagger.
 */
class MovieDetailsViewModelFactory @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val navigator: MovieDetailsNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<MovieDetailsViewModel> {

    override fun create(handle: SavedStateHandle): MovieDetailsViewModel {
        return MovieDetailsViewModel(
            getMovieDetailUseCase,
            navigator,
            ioDispatcher,
            handle
        )
    }
}
