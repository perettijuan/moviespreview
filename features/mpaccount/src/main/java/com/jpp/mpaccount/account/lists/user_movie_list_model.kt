package com.jpp.mpaccount.account.lists

import androidx.annotation.StringRes
import androidx.paging.PagedList
import com.jpp.mpaccount.R

/*
 * Contains the definitions for the entire model used in the user account feature.
 */

sealed class UserMovieListViewState {
    /*
     * Shows the not connected to network state
     */
    object ShowNotConnected : UserMovieListViewState()

    /*
     * Shows when the VM indicates that a work is in progress.
     */
    object ShowLoading : UserMovieListViewState()

    /*
     * Shows the generic error screen.
     */
    object ShowError : UserMovieListViewState()

    /*
     * Shows the list of movies.
     */
    data class ShowMovieList(val pagedList: PagedList<UserMovieItem>) : UserMovieListViewState()
}

/**
 * Represents all the navigation events that the user movie list view will response to.
 */
sealed class UserMovieListNavigationEvent {
    /*
     * Redirects the user to the previous step
     */
    object GoToUserAccount : UserMovieListNavigationEvent()

    /*
     * Redirects the user to the details of the selected movie.
     */
    data class GoToMovieDetails(val movieId: String, val movieImageUrl: String, val movieTitle: String, var positionInList: Int) : UserMovieListNavigationEvent()
}

data class UserMovieItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String
)

/**
 * Represents the type of user movie list that the application can
 * show.
 */
enum class UserMovieListType(@StringRes val titleRes: Int) {
    FAVORITE_LIST(R.string.user_account_favorite_title),
    RATED_LIST(R.string.user_account_rated_title),
    WATCH_LIST(R.string.user_account_watchlist_title)
}

/**
 * The initialization parameter used for
 * [UserMovieListViewModel].
 */
data class UserMovieListParam(
        val section: UserMovieListType,
        val posterSize: Int,
        val backdropSize: Int
) {
    companion object {
        fun favorite(posterSize: Int, backdropSize: Int) = UserMovieListParam(UserMovieListType.FAVORITE_LIST, posterSize, backdropSize)
        fun rated(posterSize: Int, backdropSize: Int) = UserMovieListParam(UserMovieListType.RATED_LIST, posterSize, backdropSize)
        fun watchlist(posterSize: Int, backdropSize: Int) = UserMovieListParam(UserMovieListType.WATCH_LIST, posterSize, backdropSize)
    }
}