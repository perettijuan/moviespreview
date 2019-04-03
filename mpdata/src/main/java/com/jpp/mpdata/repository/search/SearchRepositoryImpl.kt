package com.jpp.mpdata.repository.search

import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.SearchRepository

/**
 * [SearchRepository] implementation. Search does not supports caching, therefore the repository
 * is only accessing the data in the API.
 */
class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {
    override fun searchPage(query: String, page: Int, language: SupportedLanguage): SearchPage? = searchApi.performSearch(query, page, language)
}