package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.*

/**
 * Repository definition to support all information related to user accounts.
 *
 * This is a 'reactive' repository in the sense that it provides a mechanism to notify
 * the interested clients whenever the data that is being stored under neath is updated.
 * That way, the clients can react and refresh the data.
 */
interface AccountRepository {

    /**
     * Indicates all updates that the repository can trigger when the data managed by it
     * is updated/refreshed.
     */
    sealed class AccountDataUpdate {
        object FavoritesMovies : AccountDataUpdate()
    }

    /**
     * Subscribe to the [LiveData] whenever you need to update the state based on the data
     * that is being handled by this repository.
     */
    fun updates(): LiveData<AccountDataUpdate>

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

    /**
     * Called when all data stored underneath this repository should be cleared.
     */
    fun refresh()
}