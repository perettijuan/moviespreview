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
 * Represents the data rendered in the header view of the account fragment.
 */
data class AccountHeaderItem(
        val avatarUrl: String,
        val userName: String,
        val accountName: String
)
