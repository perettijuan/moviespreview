package com.jpp.mpdomain.repository.search

import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.paging.MPPagingDataSourceFactory

/**
 * Repository definition to perform a search.
 */
interface SearchRepository {

    /**
     * Creates a [MPPagingDataSourceFactory] that can be used to perform a search.
     */
    fun search(query: String): MPPagingDataSourceFactory<SearchResult>
}