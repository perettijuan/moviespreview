package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.paging.MPPagingDataSourceFactory

/**
 * Represents the result of a search execution.
 */
sealed class SearchUseCaseResult {
    /**
     * Represents a situation where the application has no internet connection.
     */
    object ErrorNoConnectivity : SearchUseCaseResult()

    /**
     * Represents a success case where the search can be executed. The provided
     * [MPPagingDataSourceFactory] can be used to create a [PagedList] that will
     * be used to fetch the [SearchResult] in different pages.
     */
    data class Success(val dsFactory: MPPagingDataSourceFactory<SearchResult>) : SearchUseCaseResult()
}