package com.jpp.mp.main.discover

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetDiscoveredMoviePageUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [DiscoverMoviesViewModel] instances
 * with the dependencies provided by Dagger.
 */
class DiscoverMoviesViewModelFactory @Inject constructor(
    private val getDiscoveredMoviePageUseCase: GetDiscoveredMoviePageUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<DiscoverMoviesViewModel> {

    override fun create(handle: SavedStateHandle): DiscoverMoviesViewModel {
        return DiscoverMoviesViewModel(
            getDiscoveredMoviePageUseCase,
            ioDispatcher,
            handle
        )
    }
}