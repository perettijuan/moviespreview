package com.jpp.mpdata.datasources.moviepage

import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection

/**
 * Defines the signature for the database that stores entities related to movies.
 */
interface MoviePageDb {
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

    /**
     * Flushes out all [MoviePage] data stored in the provided [section].
     */
    fun flushAllPagesInSection(section: MovieSection)

    /**
     * @return a [MoviePage] from the favorite list.
     */
    fun getFavoriteMovies(page: Int): MoviePage?

    /**
     * Stores the provided [MoviePage] as a new page of favorite movies.
     */
    fun saveFavoriteMoviesPage(page: Int, moviePage: MoviePage)

    /**
     * Flushes out any favorite [MoviePage] stored.
     */
    fun flushFavoriteMoviePages()

    /**
     * @return a [MoviePage] from the rated list.
     */
    fun getRatedMovies(page: Int): MoviePage?

    /**
     * Stores the provided [MoviePage] as a new page of rated movies.
     */
    fun saveRatedMoviesPage(page: Int, moviePage: MoviePage)

    /**
     * Flushes out any rated [MoviePage] stored.
     */
    fun flushRatedMoviePages()

    /**
     * @return a [MoviePage] from the watchlist.
     */
    fun getWatchlistMoviePage(page: Int): MoviePage?

    /**
     * Stores the provided [MoviePage] as a new page of watchlist.
     */
    fun saveWatchlistMoviePage(page: Int, moviePage: MoviePage)

    /**
     * Flushes out any [MoviePage] in the watchlist stored locally.
     */
    fun flushWatchlistMoviePages()
}
