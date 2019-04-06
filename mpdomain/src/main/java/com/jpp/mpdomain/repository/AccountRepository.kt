package com.jpp.mpdomain.repository

import com.jpp.mpdomain.MovieAccountState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * Repository definition to support all information related to user accounts.
 */
interface AccountRepository {
    fun getUserAccount(session: Session): UserAccount?

    /**
     * @return the [MovieAccountState] of a particular movie identified by [movieId].
     */
    fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState?

    /**
     * Updates the favorite state of the provided [movieId] for the current user.
     * @return true if the favorite state can be updated, false any other case.
     */
    fun updateMovieFavoriteState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean
}