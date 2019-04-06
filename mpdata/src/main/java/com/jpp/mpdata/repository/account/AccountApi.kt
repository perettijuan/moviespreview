package com.jpp.mpdata.repository.account

import com.jpp.mpdomain.MovieAccountState
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

interface AccountApi {
    fun getUserAccountInfo(session: Session): UserAccount?
    /**
     * @return the [MovieAccountState] for the provided [movieId] and the [session]. If
     * an error is detected, returns null.
     */
    fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState?
    /**
     * Updates the favorite state of the provided [movieId] for the current user.
     * @return true if the favorite state can be updated, false any other case.
     */
    fun updateMovieFavoriteState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean?
}