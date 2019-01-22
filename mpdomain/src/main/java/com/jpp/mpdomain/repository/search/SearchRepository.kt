package com.jpp.mpdomain.repository.search

import com.jpp.mpdomain.SearchResult

/**
 * Repository definition to perform a search.
 */
interface SearchRepository {

    /**
     * Performs a search using the provided [query] and returns a [SearchListing] that can be used to
     * show the list of search results.
     * [query] - the string that identifies the search to perform.
     * [imageSizeTarget] - the size of the images to configure the [SearchResult] images paths.
     * [mapper] - a mapping function to transform domain objects into another layer objects.
     */
    fun <T> search(query: String,
                   imageSizeTarget: Int,
                   mapper: (SearchResult) -> T) : SearchListing<T>
}