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

    fun withAvatar(
        userName: String,
        accountName: String,
        avatarUrl: String,
        avatarCallback: (() -> Unit)
    ) = UserAccountContentViewState(
        visibility = View.VISIBLE,
        userName = userName,
        accountName = accountName,
        avatarViewState = avatarViewState.createAvatar(avatarUrl, avatarCallback)
    )

    fun withLetter(
        userName: String,
        accountName: String,
        defaultLetter: String
    ) = UserAccountContentViewState(
        visibility = View.VISIBLE,
        userName = userName,
        accountName = accountName,
        avatarViewState = avatarViewState.createLetter(defaultLetter)
    )
}