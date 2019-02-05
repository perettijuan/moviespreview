package com.jpp.mpdata.repository.search

import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.paging.MPPagingDataSourceFactory
import com.jpp.mpdomain.repository.search.SearchRepository

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {

    override fun search(query: String): MPPagingDataSourceFactory<SearchResult> {
        return MPPagingDataSourceFactory { page, callback ->
            searchMoviePage(query, page, callback)
        }
    }

    /**
     * This is the method that does the actual work of the repository (and it is called each
     * time that the DataSource detects that a new page is needed).
     * It retrieves the data from the API and post the result as needed.
     * Note that that the results of a search are not stored in the database for the moment.
     */
    private fun searchMoviePage(queryString: String, page: Int, callback: (List<SearchResult>, Int) -> Unit) {
        searchApi.performSearch(queryString, page)?.let { searchPage ->
            searchPage
                    .results
                    .filter { it.isMovie() || it.isPerson() } // for the moment, only person and movie are valid searches
                    .let {
                        callback(it, page + 1)
                    }
        } ?: run {
            // on error detected, clear the content of the search.
            callback(listOf(), page + 1)
        }
    }
}