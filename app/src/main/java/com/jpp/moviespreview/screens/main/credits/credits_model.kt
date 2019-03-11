package com.jpp.moviespreview.screens.main.credits

/**
 * Represents the view state of the credits fragment.
 */
sealed class CreditsViewState {
    object Loading : CreditsViewState()
    object ErrorUnknown : CreditsViewState()
    object ErrorNoConnectivity : CreditsViewState()
    data class ShowCredits(val credits: List<CreditPerson>) : CreditsViewState()
}

/**
 * Represents the navigation events that can be routed through the credits section.
 */
sealed class CreditsNavigationEvent {
    data class ToPerson(val personId: String, val personImageUrl: String, val personName: String) : CreditsNavigationEvent()
}

/**
 * Represents a person in the credits list. It might be a character or
 * a crew member.
 */
data class CreditPerson(val id: Double,
                        val profilePath: String,
                        val title: String,
                        val subTitle: String)