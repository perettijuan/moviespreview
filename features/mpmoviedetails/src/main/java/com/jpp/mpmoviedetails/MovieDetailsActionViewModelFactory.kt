package com.jpp.mpmoviedetails

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetMovieStateUseCase
import com.jpp.mpdomain.usecase.UpdateFavoriteMovieStateUseCase
import com.jpp.mpdomain.usecase.UpdateWatchlistMovieStateUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [MovieDetailsActionViewModel] instances
 * with the dependencies provided by Dagger.
 */
class MovieDetailsActionViewModelFactory @Inject constructor(
    private val getMovieStateUseCase: GetMovieStateUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteMovieStateUseCase,
    private val updateWatchListUseCase: UpdateWatchlistMovieStateUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<MovieDetailsActionViewModel> {

    override fun create(handle: SavedStateHandle): MovieDetailsActionViewModel {
        return MovieDetailsActionViewModel(
            getMovieStateUseCase,
            updateFavoriteUseCase,
            updateWatchListUseCase,
            ioDispatcher,
            handle
        )
    }
}
