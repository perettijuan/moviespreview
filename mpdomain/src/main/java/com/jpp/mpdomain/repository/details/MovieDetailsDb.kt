package com.jpp.mpdomain.repository.details

import com.jpp.mpdomain.MovieDetail

/**
 * Defines the signature for the database that stores entities related to movie details.
 */
interface MovieDetailsDb {

    /**
     * @return a [MovieDetail] for the provided [movieId] if any is found, null
     * any other case.
     */
    fun getMovieDetails(movieId: Double): MovieDetail?

    /**
     * Stores the provided [MovieDetail] in the local database.
     */
    fun saveMovieDetails(movieDetail: MovieDetail)
}