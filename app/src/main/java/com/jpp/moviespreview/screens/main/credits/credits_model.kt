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
 * Represents a person in the credits list. It might be a character or
 * a crew member.
 */
data class CreditPerson(var id: Double,
                        var profilePath: String,
                        var title: String,
                        var subTitle: String)