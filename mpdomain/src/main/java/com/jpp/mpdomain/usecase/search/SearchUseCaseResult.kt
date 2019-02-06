package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.SearchPage

/**
 * Represents the result of a searchPage execution.
 */
sealed class SearchUseCaseResult {
    /**
     * Represents a situation where the application has no internet connection.
     */
    object ErrorNoConnectivity : SearchUseCaseResult()
    object ErrorUnknown : SearchUseCaseResult()


    //TODO JPP add doc
    data class Success(val result: SearchPage) : SearchUseCaseResult()
}