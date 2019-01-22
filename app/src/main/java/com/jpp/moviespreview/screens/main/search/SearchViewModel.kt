package com.jpp.moviespreview.screens.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.moviespreview.screens.CoroutineDispatchers
import com.jpp.moviespreview.screens.MPScopedViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchViewModel @Inject constructor(
        dispatchers: CoroutineDispatchers) : MPScopedViewModel(dispatchers) {

    private val viewState by lazy { MutableLiveData<SearchViewState>() }
    private var currentSearch = EMPTY_SEARCH


    fun search(searchText: String) {
        if (searchText == currentSearch) {
            return
        }
        currentSearch = searchText
        viewState.postValue(SearchViewState.Searching)
        launch {
            viewState.postValue(withContext(dispatchers.default()) { performSearch() })
        }
    }

    fun viewState(): LiveData<SearchViewState> = viewState

    fun clearSearch() {
        currentSearch = EMPTY_SEARCH
        viewState.postValue(SearchViewState.Idle)
    }


    private fun performSearch() : SearchViewState {
        Thread.sleep(4000)
        return SearchViewState.DoneSearching
    }

    companion object {
        const val EMPTY_SEARCH = ""
    }

}