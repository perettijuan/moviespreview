package com.jpp.mpaccount.account

import com.jpp.mpaccount.R

/**
 * Represents the view state of the list of movies the user has related to the account - either in
 * the favorite list, in the rated list and/or in the watchlist.
 */
internal data class UserMoviesViewState(
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