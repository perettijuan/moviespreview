package com.jpp.mpaccount.account

import android.view.View

/**
 * Represents the view state of the user's account data.
 */
internal data class UserAccountContentViewState(
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