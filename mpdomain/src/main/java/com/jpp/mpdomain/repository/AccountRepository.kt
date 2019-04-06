package com.jpp.mpdomain.repository

import com.jpp.mpdomain.*

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

    /**
     * @return the [MoviePage] that contains the favorite movies of the user.
     */
    fun getFavoriteMovies(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage?
}