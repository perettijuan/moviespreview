package com.jpp.mp.screens.main.header

/**
 * represents the view state of the [NavigationHeaderFragment].
 */
sealed class HeaderViewState {
    object Loading : HeaderViewState()
    object Login : HeaderViewState()
    data class WithInfo(val accountInfo: HeaderAccountInfo) : HeaderViewState()
}

/**
 * Represents the information of the account shown in the header view.
 */
data class HeaderAccountInfo(
        val avatarUrl: String,
        val userName: String,
        val accountName: String
)