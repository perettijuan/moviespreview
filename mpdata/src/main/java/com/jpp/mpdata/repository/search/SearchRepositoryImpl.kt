package com.jpp.mpdata.repository.search

import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.SearchRepository

/**
 * [SearchRepository] implementation. Search does not supports caching, therefore the repository
 * is only accessing the data in the API.
 */
class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {
    override suspend fun searchPage(query: String, page: Int, language: SupportedLanguage): SearchPage? = searchApi.performSearch(query, page, language)
    override suspend fun flushSearch() {
        /*
         * Searches are not being stored for the moment. It will, eventually, and then
         * we should flush the cache.
         */
    }
}
