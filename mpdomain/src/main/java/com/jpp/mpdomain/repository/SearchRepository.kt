package com.jpp.mpdomain.repository

import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SupportedLanguage

/**
 * Repository definition to perform a search.
 * The server supports searching of pages, that's why this definition allows to specify the index
 * of the page that is needed for the current search.
 */
interface SearchRepository {

    /**
     * Performs a search for the provided [query] and the given [page].
     * @return the [SearchPage] that corresponds to the [query] and the [page]. Null if
     * no one can be found.
     */
    suspend fun searchPage(query: String, page: Int, language: SupportedLanguage): SearchPage?

    /**
     * Flushes out any inner data stored related to a search in progress.
     */
    suspend fun flushSearch()
}
