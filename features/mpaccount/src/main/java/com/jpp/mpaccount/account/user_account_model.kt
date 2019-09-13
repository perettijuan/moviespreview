package com.jpp.mpaccount.account

import androidx.annotation.StringRes
import com.jpp.mpaccount.R
import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/*
 * Contains the definitions for the entire model used in the user account feature.
 */

/**************************************************************************************************
 *************************************** VIEW STATES **********************************************
 **************************************************************************************************/

/**
 * Represents the view state that the user account view can assume at any given moment.
 */
data class UserAccountViewState(
        @StringRes val screenTitle: Int = R.string.account_title,
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val contentViewState: UserAccountContentViewState = UserAccountContentViewState()
) {
    companion object {
        fun showLoading() = UserAccountViewState(loadingVisibility = View.VISIBLE)
        fun showNoConnectivityError(errorHandler: () -> Unit) = UserAccountViewState(
                errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )

        fun showUnknownError(errorHandler: () -> Unit) = UserAccountViewState(
                errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )

        fun showContentWithAvatar(
                userName: String,
                accountName: String,
                favoriteMovieState: UserMoviesViewState,
                ratedMovieState: UserMoviesViewState,
                watchListState: UserMoviesViewState,
                avatarUrl: String,
                avatarCallback: (() -> Unit)
        ) = UserAccountViewState(contentViewState = UserAccountContentViewState.withAvatar(
                userName,
                accountName,
                favoriteMovieState,
                ratedMovieState,
                watchListState,
                avatarUrl,
                avatarCallback
        ))

        fun showContentWithLetter(
                userName: String,
                accountName: String,
                favoriteMovieState: UserMoviesViewState,
                ratedMovieState: UserMoviesViewState,
                watchListState: UserMoviesViewState,
                defaultLetter: String
        ) = UserAccountViewState(contentViewState = UserAccountContentViewState.withLetter(
                userName,
                accountName,
                favoriteMovieState,
                ratedMovieState,
                watchListState,
                defaultLetter
        ))
    }
}

/**
 * Represents the view state of the user's account data.
 */
data class UserAccountContentViewState(
        val visibility: Int = View.INVISIBLE,
        val userName: String = "",
        val accountName: String = "",
        val avatarViewState: AccountAvatarViewState = AccountAvatarViewState(),
        val favoriteMovieState: UserMoviesViewState = UserMoviesViewState(),
        val ratedMovieState: UserMoviesViewState = UserMoviesViewState(),
        val watchListState: UserMoviesViewState = UserMoviesViewState()
) {
    companion object {
        fun withAvatar(
                userName: String,
                accountName: String,
                favoriteMovieState: UserMoviesViewState,
                ratedMovieState: UserMoviesViewState,
                watchListState: UserMoviesViewState,
                avatarUrl: String,
                avatarCallback: (() -> Unit)
        ) = UserAccountContentViewState(
                visibility = View.VISIBLE,
                userName = userName,
                accountName = accountName,
                avatarViewState = AccountAvatarViewState.createAvatar(avatarUrl, avatarCallback),
                favoriteMovieState = favoriteMovieState,
                ratedMovieState = ratedMovieState,
                watchListState = watchListState
        )

        fun withLetter(
                userName: String,
                accountName: String,
                favoriteMovieState: UserMoviesViewState,
                ratedMovieState: UserMoviesViewState,
                watchListState: UserMoviesViewState,
                defaultLetter: String
        ) = UserAccountContentViewState(
                visibility = View.VISIBLE,
                userName = userName,
                accountName = accountName,
                avatarViewState = AccountAvatarViewState.createLetter(defaultLetter),
                favoriteMovieState = favoriteMovieState,
                ratedMovieState = ratedMovieState,
                watchListState = watchListState
        )
    }
}

/**
 * ViewState that represents the state of the avatar. If the avatar can be downloaded
 * as an image, then [avatarVisibility] will view [View.VISIBLE] and the default letter
 * will be hidden. If there's an error when the avatar is being downloaded, then the
 * view state will show the default letter and will hide the avatar.
 */
data class AccountAvatarViewState(
        val avatarUrl: String? = null,
        val avatarVisibility: Int = View.INVISIBLE,
        val avatarErrorCallback: (() -> Unit)? = null,
        val defaultLetter: String = "",
        val defaultLetterVisibility: Int = View.INVISIBLE
) {
    companion object {
        fun createAvatar(avatarUrl: String, callback: (() -> Unit)) = AccountAvatarViewState(
                avatarUrl = avatarUrl,
                avatarVisibility = View.VISIBLE,
                avatarErrorCallback = callback)

        fun createLetter(defaultLetter: String) = AccountAvatarViewState(
                defaultLetter = defaultLetter,
                defaultLetterVisibility = View.VISIBLE,
                avatarVisibility = View.INVISIBLE)
    }
}

/**
 * Represents the view state of the list of movies the user has related to the account - either in
 * the favorite list, in the rated list and/or in the watchlist.
 */
data class UserMoviesViewState(
        val errorText: Int = 0,
        val items: List<UserMovieItem>? = null
) {
    companion object {

        fun createFavoriteEmpty() = UserMoviesViewState(
                errorText = R.string.user_account_no_favorite_movies
        )

        fun createRatedEmpty() = UserMoviesViewState(
                errorText = R.string.user_account_no_rated_movies
        )

        fun createWatchlistEmpty() = UserMoviesViewState(
                errorText = R.string.user_account_no_watchlist_movies
        )

        fun createWithItems(items: List<UserMovieItem>) = UserMoviesViewState(
                items = items
        )

        fun createError() = UserMoviesViewState(
                errorText = R.string.user_account_favorite_movies_error
        )

    }
}


/**
 * Represents an item shown in the user movies section.
 */
data class UserMovieItem(val image: String) : UserAccountMoviesView.UserAccountMovieItem {
    override fun getImageUrl(): String = image
}

/**************************************************************************************************
 *************************************** NAVIGATION ***********************************************
 **************************************************************************************************/

/**
 * Represents all the navigation events that the user account view will response to.
 */
sealed class UserAccountNavigationEvent {
    /*
     * Used to redirect the previous step.
     */
    object GoToPrevious : UserAccountNavigationEvent()

    /*
     * Used to navigate to the favorite movies section.
     */
    object GoToFavorites : UserAccountNavigationEvent()

    /*
     * Used to navigate to the rated movies section.
     */
    object GoToRated : UserAccountNavigationEvent()

    /*
     * Used to navigate to the watchlist movies section.
     */
    object GoToWatchlist : UserAccountNavigationEvent()
}
