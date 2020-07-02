package com.jpp.mpdata.datasources.moviestate

import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * API definition to manipulate all the [MovieState] data in the remote resources.
 */
interface MovieStateApi {
    /**
     * @return the [MovieState] for the provided [movieId] and the [session]. If
     * an error is detected, returns null.
     */
    fun getMovieAccountState(movieId: Double, session: Session): MovieState?

    /**
     * Updates the favorite state of the provided [movieId] for the current user.
     * @return true if the favorite state can be updated, false any other case.
     */
    fun updateFavoriteMovieState(
        movieId: Double,
        asFavorite: Boolean,
        userAccount: UserAccount,
        session: Session
    ): Boolean?

    /**
     * Updates the watchlist state of the provided [movieId] for the current user.
     * @return true if the watchlist state of the movie can be updated, false any other case.
     */
    fun updateWatchlistMovieState(
        movieId: Double,
        inWatchList: Boolean,
        userAccount: UserAccount,
        session: Session
    ): Boolean?

    /**
     * Rates the movie identified by [movieId] with the provided [rating].
     * @return true if the movie has been rated, false any other case.
     */
    fun rateMovie(
        movieId: Double,
        rating: Float,
        userAccount: UserAccount,
        session: Session
    ): Boolean?

    /**
     * Deletes the rating that the user has set for the movie identified by [movieId].
     * @return true if the movie rating has been rated, false any other case.
     */
    fun deleteMovieRating(movieId: Double, session: Session): Boolean?
}
