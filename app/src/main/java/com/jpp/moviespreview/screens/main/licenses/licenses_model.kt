package com.jpp.moviespreview.screens.main.licenses

/**
 * Represents the view state of the results fragment.
 */
sealed class LicensesViewState {
    object Loading : LicensesViewState()
    object ErrorUnknown : LicensesViewState()
    data class Loaded(val licenses: List<LicenseItem>) : LicensesViewState()
}


/**
 * Represents all navigation events that can be routed in licenses section.
 */
sealed class LicensesNavEvent {
    data class ToLicenseContent(val licenseName: String, val licenseId: Int) : LicensesNavEvent()
}


/**
 * Represents an item in the list of results shown.
 */
data class LicenseItem(val id: Int,
                       val name: String)