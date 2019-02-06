package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.SearchPage
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.repository.SearchRepository

/**
 * Defines a UseCase that executes the searchPage.
 */
interface SearchUseCase {
    /**
     * Performs a searchPage based in the provided [query].
     * @return
     *          - [SearchUseCaseResult.ErrorNoConnectivity] when the UC detects that the application has no internet connectivity.
     *          - [SearchUseCaseResult.Success] when there is internet connectivity and the searchPage can be executed.
     */
    fun search(query: String, page: Int): SearchUseCaseResult


    class Impl(private val searchRepository: SearchRepository,
               private val connectivityHandler: ConnectivityHandler) : SearchUseCase {

        override fun search(query: String, page: Int): SearchUseCaseResult {
            return when (connectivityHandler.isConnectedToNetwork()) {
                false -> SearchUseCaseResult.ErrorNoConnectivity
                true -> searchRepository.searchPage(query, page)?.let {
                    SearchUseCaseResult.Success(sanitizeSearchPage(it))
                } ?: run {
                    SearchUseCaseResult.ErrorUnknown
                }
            }
        }


        private fun sanitizeSearchPage(searchPage: SearchPage): SearchPage {
            return searchPage.copy(
                    results = searchPage
                            .results
                            .filter { it.isMovie() || it.isPerson() } // for the moment, only person and movie are valid searches
            )
        }
    }
}