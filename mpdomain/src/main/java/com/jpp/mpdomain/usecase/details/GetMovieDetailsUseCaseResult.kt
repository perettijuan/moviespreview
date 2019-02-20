package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.MovieDetail

/**
 * Represents the result of fetching details for a given movie.
 */
sealed class GetMovieDetailsUseCaseResult {
    object ErrorNoConnectivity : GetMovieDetailsUseCaseResult()
    object ErrorUnknown : GetMovieDetailsUseCaseResult()
    data class Success(val details: MovieDetail) : GetMovieDetailsUseCaseResult()
}