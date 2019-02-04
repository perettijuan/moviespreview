package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.paging.MPPagingDataSourceFactory

sealed class SearchUseCaseResult {
    object ErrorUnknown : SearchUseCaseResult()
    object ErrorNoConnectivity : SearchUseCaseResult()
    data class Success(val dsFactory: MPPagingDataSourceFactory<SearchResult>) : SearchUseCaseResult()
}

interface SearchUseCase {
    fun search(query: String) : SearchUseCaseResult
}

interface ConfigSearchResultUseCase {
    fun configure(imageTargetSize: Int, result: SearchResult): SearchResult
}