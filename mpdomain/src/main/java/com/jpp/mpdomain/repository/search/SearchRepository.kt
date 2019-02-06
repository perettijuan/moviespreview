package com.jpp.mpdomain.repository.search

import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.paging.MPPagingDataSourceFactory

/**
 * Repository definition to perform a search.
 */
interface SearchRepository {

    //TODO JPP add doc
    fun search(query: String, page: Int): SearchPage?
}