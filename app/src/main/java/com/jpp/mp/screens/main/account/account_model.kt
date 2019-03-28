package com.jpp.mp.screens.main.account

import com.jpp.mpdomain.AccessToken

/**
 * Represents all the view states that the AccountFragment can assume.
 */
sealed class AccountViewState {
    object Loading : AccountViewState()
    object ErrorUnknown : AccountViewState()
    object ErrorNoConnectivity : AccountViewState()
    object SessionCreated : AccountViewState()// TODO JPP this should be another class that represents the user account data
    data class RenderlURL(val url: String, val interceptUrl: String, val accessToken: AccessToken) : AccountViewState()
}
