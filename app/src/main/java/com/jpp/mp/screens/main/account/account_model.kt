package com.jpp.mp.screens.main.account

import com.jpp.mpdomain.AccessToken

/**
 * Represents all the view states that the AccountFragment can assume.
 */
sealed class AccountViewState {
    object Loading : AccountViewState()
    object ErrorUnknown : AccountViewState()
    object ErrorNoConnectivity : AccountViewState()
    data class Oauth(val url: String, val interceptUrl: String, val accessToken: AccessToken, val reminder: Boolean = false) : AccountViewState()
    data class AccountContent(val headerItem: AccountHeaderItem) : AccountViewState()
}

/**
 * Represents all the view states that the favorite section can assume in the AccountFragment.
 */
sealed class FavoriteMoviesViewState {
    object Loading : FavoriteMoviesViewState()
    object NoFavoriteMovies : FavoriteMoviesViewState()
    object UnableToLoad: FavoriteMoviesViewState()
    data class FavoriteMovies(val movies: List<FavoriteMovie>) : FavoriteMoviesViewState()
}

/**
 * Represents the data rendered in the header view of the account fragment.
 */
data class AccountHeaderItem(
        val avatarUrl: String,
        val userName: String,
        val accountName: String,
        val defaultLetter: Char
)

data class FavoriteMovie(val title: String,
                         val posterPath: String)
