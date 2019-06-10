package com.jpp.mpdata.datasources.account

import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

interface AccountApi {
    fun getUserAccountInfo(session: Session): UserAccount?
    /**
     * Updates the favorite state of the provided [movieId] for the current user.
     * @return true if the favorite state can be updated, false any other case.
     */
    fun updateMovieFavoriteState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean?
}