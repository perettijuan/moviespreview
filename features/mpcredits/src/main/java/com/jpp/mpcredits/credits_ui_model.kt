package com.jpp.mpcredits

import android.view.View
import com.jpp.mpcredits.CreditsErrorViewState.Companion.asConnectivity
import com.jpp.mpcredits.CreditsErrorViewState.Companion.asUnknownError

/**
 * Represents the view state of the credits screen.
 */
data class CreditsViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: CreditsErrorViewState = CreditsErrorViewState(),
        val creditsViewState: CreditsContentViewState = CreditsContentViewState(),
        val noCreditsViewState: NoCreditsAvailableViewState = NoCreditsAvailableViewState()) {

    companion object {
        fun showLoading() = CreditsViewState(loadingVisibility = View.VISIBLE)
        fun showUnknownError(errorHandler: () -> Unit) = CreditsViewState(errorViewState = asUnknownError(errorHandler))
        fun showNoConnectivityError(errorHandler: () -> Unit) = CreditsViewState(errorViewState = asConnectivity(errorHandler))
        fun showCredits(creditItems: List<CreditPerson>) = CreditsViewState(creditsViewState = CreditsContentViewState.creditList(creditItems))
        fun showNoCreditsAvailable() = CreditsViewState(noCreditsViewState = NoCreditsAvailableViewState.noDataAvailable())
    }

}

/**
 * Represents the state of the error view.
 */
data class CreditsErrorViewState(val visibility: Int = View.INVISIBLE,
                                 val isConnectivity: Boolean = false,
                                 val errorHandler: (() -> Unit)? = null) {

    companion object {
        fun asConnectivity(handler: () -> Unit) = CreditsErrorViewState(
                visibility = View.VISIBLE,
                isConnectivity = true,
                errorHandler = handler)

        fun asUnknownError(handler: () -> Unit) = CreditsErrorViewState(
                visibility = View.VISIBLE,
                isConnectivity = false,
                errorHandler = handler)
    }
}

/**
 * Represents the view state of the no credits available view.
 */
data class NoCreditsAvailableViewState(
        val visibility: Int = View.INVISIBLE,
        val titleRes: Int = R.string.no_credits_for_this_movie
) {
    companion object {
        fun noDataAvailable() = NoCreditsAvailableViewState(visibility = View.VISIBLE)
    }
}

/**
 * Represents the state of the credits list in the credits UI.
 */
data class CreditsContentViewState(
        val visibility: Int = View.INVISIBLE,
        val creditItems: List<CreditPerson> = listOf()
) {
    companion object {
        fun creditList(items: List<CreditPerson>) = CreditsContentViewState(
                visibility = View.VISIBLE,
                creditItems = items
        )
    }
}

/**
 * Represents a person in the credits list. It might be a character or
 * a crew member.
 */
data class CreditPerson(val id: Double,
                        val profilePath: String,
                        val title: String,
                        val subTitle: String)

/**
 * Represents the navigation events that can be routed through the credits section.
 */
sealed class CreditsNavigationEvent {
    data class ToPerson(val personId: String, val personImageUrl: String, val personName: String) : CreditsNavigationEvent()
}