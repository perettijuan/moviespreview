package com.jpp.mpdomain.usecase.movies

import com.jpp.mpdomain.MoviePage

/**
 * Represents the result of a a movies fetching execution.
 */
sealed class GetMoviesResult {
    object ErrorNoConnectivity : GetMoviesResult()
    object ErrorUnknown : GetMoviesResult()
    data class Success(val moviesPage: MoviePage) : GetMoviesResult()
}