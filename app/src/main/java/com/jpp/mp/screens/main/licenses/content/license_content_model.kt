package com.jpp.mp.screens.main.licenses.content

/**
 * Represents the view state of the results fragment.
 */
sealed class LicenseViewState {
    object Loading : LicenseViewState()
    object ErrorUnknown : LicenseViewState()
    data class Loaded(val contentUrl: String) : LicenseViewState()
}