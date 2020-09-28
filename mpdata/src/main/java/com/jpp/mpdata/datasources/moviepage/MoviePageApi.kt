package com.jpp.mpdata.datasources.moviepage

import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.UserAccount

/**
 * API definition to retrieve all movies related data from the server.
 */
interface MoviePageApi {
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
     * @return the [MoviePage] that contains the favorite movies of the user.
     */
    fun getFavoriteMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage?

    /**
     * @return the [MoviePage] that contains the movies that the user has rated.
     */
    fun getRatedMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage?

    /**
     * @return the [MoviePage] that contains the watchlist of movies.
     */
    fun getWatchlistMoviePage(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage?

    /**
     * TODO JPP add javadoc
     */
    fun discover(page: Int): MoviePage?
}
