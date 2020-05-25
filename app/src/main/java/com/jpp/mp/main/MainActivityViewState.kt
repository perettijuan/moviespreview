package com.jpp.mp.main

/**
 * Represents the view state that the MainActivity can show at any given time.
 */
data class MainActivityViewState(
    val sectionTitle: String,
    val menuBarEnabled: Boolean,
    val searchEnabled: Boolean
)
