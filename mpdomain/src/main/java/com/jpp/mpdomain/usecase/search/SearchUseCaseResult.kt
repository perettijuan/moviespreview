package com.jpp.mpdomain.usecase.search

import com.jpp.mpdomain.SearchPage

/**
 * Represents the result of a searchPage execution.
 */
sealed class SearchUseCaseResult {
    object ErrorNoConnectivity : SearchUseCaseResult()
    object ErrorUnknown : SearchUseCaseResult()
    data class Success(val searchPage: SearchPage) : SearchUseCaseResult()
}