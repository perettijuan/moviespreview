package com.jpp.mpdomain.repository

import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection

/**
 * Repository definition to access all movies related data.
 */
interface MoviesRepository {

    /**
     * Retrieves a [MoviePage] for the provided [section].
     * @return The [MoviePage] indicated by [page] if any [MoviePage] exists for it.
     * Otherwise, null.
     */
    fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage?

    /**
     * Retrieves a [MovieDetail] for a particular movie.
     * @return a [MovieDetail] instance if a detail can be found for the provided [movieId],
     * null in any other case.
     */
    fun getMovieDetails(movieId: Double): MovieDetail?
}