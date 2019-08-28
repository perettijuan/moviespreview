package com.jpp.mpaccount.account.lists

import android.view.View
import androidx.annotation.StringRes
import androidx.paging.PagedList
import com.jpp.mpaccount.R
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/*
 * This file contains the definitions for the entire model used in the movies list feature.
 */

/**************************************************************************************************
 *************************************** VIEW STATES **********************************************
 **************************************************************************************************/

/**
 * Represents the view state of the user movies list screen. This indicates that the view
 * can only render the view states modeled in this class.
 */
data class UserMovieListViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val contentViewState: UserMovieListContentViewState = UserMovieListContentViewState()
) {
    companion object {
        fun showLoading() = UserMovieListViewState(loadingVisibility = View.VISIBLE)
        fun showUnknownError(errorHandler: () -> Unit) = UserMovieListViewState(errorViewState = ErrorViewState.asUnknownError(errorHandler))
        fun showNoConnectivityError(errorHandler: () -> Unit) = UserMovieListViewState(errorViewState = ErrorViewState.asConnectivity(errorHandler))
        fun showMovieList(pagedList: PagedList<UserMovieItem>) = UserMovieListViewState(contentViewState = UserMovieListContentViewState(visibility = View.VISIBLE, movieList = pagedList))
    }
}

/**
 * Represents the view state of the content shown in the movie list view.
 */
data class UserMovieListContentViewState(
        val visibility: Int = View.INVISIBLE,
        val movieList: PagedList<UserMovieItem>? = null
)

/**
 * Represents an item in the list of User Movies.
 */
data class UserMovieItem(
        val movieId: Double,
        val headerImageUrl: String,
        val title: String,
        val contentImageUrl: String
)

/**************************************************************************************************
 *************************************** NAVIGATION ***********************************************
 **************************************************************************************************/

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

/**************************************************************************************************
 *************************************** VM PARAMS ************************************************
 **************************************************************************************************/

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