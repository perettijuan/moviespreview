package com.jpp.mpdomain.repository.movies

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.jpp.mpdomain.repository.RepositoryState

/**
 * Data class that models the response of the repository layer when the feature is requesting data as
 * a list backed by the paging library.
 */
data class MovieListing<T>(
        // the LiveData of paged lists for the UI to observe - used to provide paging mechanism.
        val pagedList: LiveData<PagedList<T>>,
        // the live data that represents the retrieve movies operation state
        val operationState: LiveData<RepositoryState>,
        // a function to execute the retry mechanism
        val retry: () -> Unit
)