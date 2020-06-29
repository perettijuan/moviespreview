package com.jpp.mpmoviedetails.rates

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.DeleteMovieRatingUseCase
import com.jpp.mpdomain.usecase.GetMovieStateUseCase
import com.jpp.mpdomain.usecase.RateMovieUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

/**
 * [ViewModelAssistedFactory] to create specific [RateMovieViewModel] instances
 * with the dependencies provided by Dagger.
 */
class RateMovieViewModelFactory @Inject constructor(
    private val getMovieStateUseCase: GetMovieStateUseCase,
    private val rateMovieUseCase: RateMovieUseCase,
    private val rateMovieNavigator: RateMovieNavigator,
    private val deleteMovieRatingUseCase: DeleteMovieRatingUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<RateMovieViewModel> {

    override fun create(handle: SavedStateHandle): RateMovieViewModel {
        return RateMovieViewModel(
            getMovieStateUseCase,
            rateMovieUseCase,
            deleteMovieRatingUseCase,
            rateMovieNavigator,
            ioDispatcher,
            handle
        )
    }
}
