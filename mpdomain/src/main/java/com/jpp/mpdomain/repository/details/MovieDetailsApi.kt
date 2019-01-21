package com.jpp.mpdomain.repository.details

import com.jpp.mpdomain.MovieDetail

/**
 * API definition for the movie details section.
 */
interface MovieDetailsApi {
    /**
     * @return a [MovieDetail] for the provided [movieId] if any is found, null
     * any other case.
     */
    fun getMovieDetails(movieId: Double): MovieDetail?
}