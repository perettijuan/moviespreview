package com.jpp.mpdomain.repository

import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.SupportedLanguage

/**
 * Repository definition to access all [MovieDetail] data.
 */
interface MovieDetailRepository  {
    /**
     * Retrieves a [MovieDetail] for a particular movie.
     * @return a [MovieDetail] instance if a detail can be found for the provided [movieId],
     * null in any other case.
     */
    fun getMovieDetails(movieId: Double, language: SupportedLanguage): MovieDetail?

    /**
     * Flushes out any stored data related to movie details.
     */
    fun flushMovieDetailsData()
}