package com.jpp.mp.screens.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * [ViewModel] used to handle the SearchView interactions.
 *
 * MOTIVATION: since the SearchView used to detect user input in the searchPage feature is hosted
 * by the Activity and the Search feature is actually implemented by the SearchFragmentDeprecated, we need
 * a mechanism to communicate the SearchFragmentDeprecated with the MainActivity in order to let it
 * handle the SearchView.
 * In other words: the SearchView belongs to the MainActivity view hierarchy while the searching
 * feature is implemented in the SearchFragmentDeprecated - that has a completely different view hierarchy.
 */
//TODO JPP delete ME
class SearchViewViewModel @Inject constructor() : ViewModel() {

    private val searchLiveData by lazy { MutableLiveData<SearchEvent>() }
    private var searchText = EMPTY_SEARCH

    fun searchEvents(): LiveData<SearchEvent> = searchLiveData

    fun search(query: String) {
        if (query != EMPTY_SEARCH) {
            searchText = query
            searchLiveData.postValue(SearchEvent.Search(searchText))
        }
    }

    fun clearSearch() {
        searchText = EMPTY_SEARCH
        searchLiveData.postValue(SearchEvent.ClearSearch)
    }

    override fun onCleared() {
        searchText = EMPTY_SEARCH
        super.onCleared()
    }

    private companion object {
        const val EMPTY_SEARCH = ""
    }
}