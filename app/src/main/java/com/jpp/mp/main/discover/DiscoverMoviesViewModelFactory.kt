package com.jpp.mp.main.discover

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.GetAllMovieGenresUseCase
import com.jpp.mpdomain.usecase.GetDiscoveredMoviePageUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [DiscoverMoviesViewModel] instances
 * with the dependencies provided by Dagger.
 */
class DiscoverMoviesViewModelFactory @Inject constructor(
    private val getDiscoveredMoviePageUseCase: GetDiscoveredMoviePageUseCase,
    private val gentAllMovieGenresUseCase: GetAllMovieGenresUseCase,
    private val navigator: DiscoverMoviesNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<DiscoverMoviesViewModel> {

    override fun create(handle: SavedStateHandle): DiscoverMoviesViewModel {
        return DiscoverMoviesViewModel(
            getDiscoveredMoviePageUseCase,
            gentAllMovieGenresUseCase,
            navigator,
            ioDispatcher,
            handle
        )
    }
}
