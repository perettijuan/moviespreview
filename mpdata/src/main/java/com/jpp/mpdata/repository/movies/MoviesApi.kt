package com.jpp.mpdata.repository.movies

import com.jpp.mpdomain.*

/**
 * API definition to retrieve all movies related data from the server.
 */
interface MoviesApi {
    /**
     * @return the [MoviePage] that contains the movies being played right now.
     * Null if no data is available.
     */
    fun getNowPlayingMoviePage(page: Int, language: SupportedLanguage): MoviePage?
    /**
     * @return the [MoviePage] that contains the most popular movies.
     * Null if no data is available.
     */
    fun getPopularMoviePage(page: Int, language: SupportedLanguage): MoviePage?
    /**
     * @return the [MoviePage] that contains the top rated movies.
     * Null if no data is available.
     */
    fun getTopRatedMoviePage(page: Int, language: SupportedLanguage): MoviePage?
    /**
     * @return the [MoviePage] that contains the upcoming movies.
     * Null if no data is available.
     */
    fun getUpcomingMoviePage(page: Int, language: SupportedLanguage): MoviePage?
    /**
     * @return a [MovieDetail] for the provided [movieId] if any is found, null
     * any other case.
     */
    fun getMovieDetails(movieId: Double, language: SupportedLanguage): MovieDetail?
    /**
     * @return the [MovieAccountState] for the provided [movieId] and the [session]. If
     * an error is detected, returns null.
     */
    fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState?
    /**
     * Updates the favorite state of the provided [movie] for the current user.
     * @return true if the favorite state can be updated, false any other case.
     */
    fun updateMovieFavoriteState(movie: Movie, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean?
}