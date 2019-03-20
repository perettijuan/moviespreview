package com.jpp.mp.screens.main.header

/**
 * represents the view state of the [NavigationHeaderFragment].
 */
sealed class HeaderViewState {
    object Loading : HeaderViewState()
    object NotLogged : HeaderViewState()
}