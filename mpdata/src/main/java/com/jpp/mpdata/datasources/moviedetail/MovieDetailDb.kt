package com.jpp.mpdata.datasources.moviedetail

import com.jpp.mpdomain.MovieDetail

/**
 * Database definition to handle [MovieDetail].
 */
interface MovieDetailDb {
    /**
     * @return a [MovieDetail] for the provided [movieId] if any is found, null
     * any other case.
     */
    fun getMovieDetails(movieId: Double): MovieDetail?

    /**
     * Stores the provided [MovieDetail] in the local database.
     */
    fun saveMovieDetails(movieDetail: MovieDetail)

    /**
     * Flushes any movie detail stored data.
     */
    fun flushData()
}