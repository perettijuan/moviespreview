package com.jpp.mp.screens.main.account

/**
 * Represents all the view states that the AccountFragment can assume.
 */
sealed class AccountViewState {
    object Loading : AccountViewState()
    object ErrorUnknown : AccountViewState()
    object ErrorNoConnectivity : AccountViewState()
    data class RenderlURL(val url: String, val interceptUrl: String) : AccountViewState()
}
