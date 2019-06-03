package com.jpp.mp.screens.main.header

/**
 * represents the view state of the [NavigationHeaderFragment].
 */
sealed class HeaderViewState {
    object ShowLoading : HeaderViewState()
    object ShowLogin : HeaderViewState()
    data class ShowAccount(val avatarUrl: String,
                           val userName: String,
                           val accountName: String) : HeaderViewState()
}

/**
 * Represents the navigation events that can be routed from the navigation header.
 */
sealed class HeaderNavigationEvent {
    object ToUserAccount : HeaderNavigationEvent()
    object ToLogin : HeaderNavigationEvent()
}
