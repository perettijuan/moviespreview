package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.MoviePage

/**
 * Represents the result of a a movies fetching execution.
 */
sealed class GetMoviesUseCaseResult {
    object ErrorNoConnectivity : GetMoviesUseCaseResult()
    object ErrorUnknown : GetMoviesUseCaseResult()
    data class Success(val moviesPage: MoviePage) : GetMoviesUseCaseResult()
}