package com.jpp.mpdomain.repository.search

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * Data class that models the response of the search repository.
 * Provides a [PagedList] instance to use with the Android Paging Library and a [LiveData]
 * object that represents the state of the repository via [SearchRepositoryState].
 */
data class SearchListing<T>(
        val pagedList: LiveData<PagedList<T>>,
        val opState: LiveData<SearchRepositoryState>,
        val retry: () -> Unit
)