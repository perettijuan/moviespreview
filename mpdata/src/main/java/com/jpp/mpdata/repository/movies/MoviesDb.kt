package com.jpp.mpdata.repository.movies

import com.jpp.mpdomain.MovieDetail
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

    /**
     * @return a [MoviePage] from the favorite list.
     */
    fun getFavoriteMovies(page: Int): MoviePage?

    /**
     * Stores the provided [MoviePage] as a new page of favorite movies.
     */
    fun saveFavoriteMoviesPage(page: Int, moviePage: MoviePage)

    /**
     * @return a [MoviePage] from the rated list.
     */
    fun getRatedMovies(page: Int): MoviePage?

    /**
     * Stores the provided [MoviePage] as a new page of rated movies.
     */
    fun saveRatedMoviesPage(page: Int, moviePage: MoviePage)

    /**
     * @return a [MoviePage] from the watchlist.
     */
    fun getWatchlistMoviePage(page: Int): MoviePage?

    /**
     * Stores the provided [MoviePage] as a new page of watchlist.
     */
    fun saveWatchlistMoviePage(page: Int, moviePage: MoviePage)

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