package com.jpp.moviespreview.screens.main

/**
 * Represents the view state that the MainActivity can show at any given time.
 */
sealed class MainActivityViewState(val menuBarEnabled: Boolean) {
    object ActionBarLocked : MainActivityViewState(true)
    data class ActionBarUnlocked(val contentImageUrl: String, val movieTitle: String) : MainActivityViewState(false)
}