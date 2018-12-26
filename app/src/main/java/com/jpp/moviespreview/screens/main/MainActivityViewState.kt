package com.jpp.moviespreview.screens.main

/**
 * Represents the view state that the MainActivity can show at any given time.
 */
sealed class MainActivityViewState {
    object ActionBarLocked : MainActivityViewState()
    data class ActionBarUnlocked(val contentImageUrl: String) : MainActivityViewState()
}