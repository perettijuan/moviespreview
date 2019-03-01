package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.MovieDetail

/**
 * Represents the result of fetching details for a given movie.
 */
sealed class GetMovieDetailsResult {
    object ErrorNoConnectivity : GetMovieDetailsResult()
    object ErrorUnknown : GetMovieDetailsResult()
    data class Success(val details: MovieDetail) : GetMovieDetailsResult()
}