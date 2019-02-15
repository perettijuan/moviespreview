package com.jpp.mpdomain.repository

import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection

/**
 * Repository definition to fetch a list of movies.
 */
interface MoviesRepository {

    /**
     * Retrieves a [MoviePage] for the provided [section].
     * @return The [MoviePage] indicated by [page] if any [MoviePage] exists for it.
     * Otherwise, null.
     */
    fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage?
}