package com.jpp.mpdata.repository.search

import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.repository.search.SearchRepository

class SearchRepositoryImpl(private val searchApi: SearchApi) : SearchRepository {
    override fun search(query: String, page: Int): SearchPage? {
        return searchApi.performSearch(query, page)
    }
}