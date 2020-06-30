package com.jpp.mpdomain.repository

import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * Repository definition to handle [MovieState].
 */
interface MovieStateRepository {
    /**
     * @return the [MovieState] that indicates the state of the provided [movieId]
     * for the [session]. If an error is detected, null is returned.
     */
    suspend fun getStateForMovie(movieId: Double, session: Session): MovieState?

    /**
     * Updates the favorite state of the movie identified by [movieId] to [asFavorite].
     */
    suspend fun updateFavoriteMovieState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean

    /**
     * Updates the watchlist state of the movie identified by [movieId] to [inWatchList].
     */
    suspend fun updateWatchlistMovieState(movieId: Double, inWatchList: Boolean, userAccount: UserAccount, session: Session): Boolean

    /**
     * Rates the movie identified by [movieId] with the provided [rating].
     */
    suspend fun rateMovie(movieId: Double, rating: Float, userAccount: UserAccount, session: Session): Boolean

    /**
     * Deletes the rating that the user has previously set for the movie identified by [movieId].
     */
    suspend fun deleteMovieRate(movieId: Double, session: Session): Boolean
}
