package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.repository.search.SearchRepository

/**
 * Defines a UseCase that executes the search.
 */
interface SearchUseCase {
    /**
     * Performs a search based in the provided [query].
     * @return
     *          - [SearchUseCaseResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *          - [SearchUseCaseResult.Success] when there is internet connectivity and the search can be executed.
     */
    fun search(query: String): SearchUseCaseResult


    class Impl(private val searchRepository: SearchRepository,
               private val connectivityHandler: ConnectivityHandler) : SearchUseCase {

        override fun search(query: String): SearchUseCaseResult {
            return when (connectivityHandler.isConnectedToNetwork()) {
                true -> SearchUseCaseResult.Success(searchRepository.search(query))
                false -> SearchUseCaseResult.ErrorNoConnectivity
            }
        }
    }
}