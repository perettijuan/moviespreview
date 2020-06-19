package com.jpp.mpsearch

import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.common.viewmodel.ViewModelAssistedFactory
import com.jpp.mpdomain.usecase.SearchUseCase
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * [ViewModelAssistedFactory] to create specific [SearchViewModel] instances
 * with the dependencies provided by Dagger.
 */
class SearchViewModelFactory @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val searchNavigator: SearchNavigator,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModelAssistedFactory<SearchViewModel> {

    override fun create(handle: SavedStateHandle): SearchViewModel {
        return SearchViewModel(searchUseCase, searchNavigator, ioDispatcher, handle)
    }
}