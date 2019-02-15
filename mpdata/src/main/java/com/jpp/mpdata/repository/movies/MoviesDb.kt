package com.jpp.mpdata.repository.movies

import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection

/**
 * Defines the signature for the database that stores entities related to movies.
 */
interface MoviesDb {
    /**
     * Searches for the [MoviePage] that identified whit the [page] that belongs to the
     * provided [section].
     * @return a [MoviePage] is any is stored in the database, null if no data is stored in the database.
     */
    fun getMoviePageForSection(page: Int, section: MovieSection): MoviePage?

    /**
     * Stores the provided [MoviePage] in the local database for the provided [section].
     */
    fun saveMoviePageForSection(moviePage: MoviePage, section: MovieSection)
}