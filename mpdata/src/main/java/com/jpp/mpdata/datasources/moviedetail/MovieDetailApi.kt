package com.jpp.mpdata.datasources.moviedetail

import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.SupportedLanguage

/**
 * API definition to access [MovieDetail] data.
 */
interface MovieDetailApi {
    /**
     * @return a [MovieDetail] for the provided [movieId] if any is found, null
     * any other case.
     */
    fun getMovieDetails(movieId: Double, language: SupportedLanguage): MovieDetail?
}
