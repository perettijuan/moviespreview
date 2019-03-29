package com.jpp.mp.screens.main.account

import com.jpp.mpdomain.AccessToken

/**
 * Represents all the view states that the AccountFragment can assume.
 */
sealed class AccountViewState {
    object Loading : AccountViewState()
    object ErrorUnknown : AccountViewState()
    object ErrorNoConnectivity : AccountViewState()
    data class Oauth(val url: String, val interceptUrl: String, val accessToken: AccessToken) : AccountViewState()
    data class AccountInfo(val accountItem: AccountItem) : AccountViewState()
}

/**
 * Represents the data rendered in the account item.
 */
data class AccountItem(
        val avatarUrl: String,
        val userName: String,
        val accountName: String
)
