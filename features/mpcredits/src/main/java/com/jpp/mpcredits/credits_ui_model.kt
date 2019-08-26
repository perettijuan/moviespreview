package com.jpp.mpcredits

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/*
 * This file contains the definitions for the entire model used in the credits feature.
 */

/**
 * Represents the view state of the credits screen.
 */
data class CreditsViewState(
        val loadingVisibility: Int = View.INVISIBLE,
        val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
        val creditsViewState: CreditsContentViewState = CreditsContentViewState(),
        val noCreditsViewState: NoCreditsAvailableViewState = NoCreditsAvailableViewState()) {

    companion object {
        fun showLoading() = CreditsViewState(loadingVisibility = View.VISIBLE)
        fun showUnknownError(errorHandler: () -> Unit) = CreditsViewState(errorViewState = ErrorViewState.asUnknownError(errorHandler))
        fun showNoConnectivityError(errorHandler: () -> Unit) = CreditsViewState(errorViewState = ErrorViewState.asConnectivity(errorHandler))
        fun showCredits(creditItems: List<CreditPerson>) = CreditsViewState(creditsViewState = CreditsContentViewState.creditList(creditItems))
        fun showNoCreditsAvailable() = CreditsViewState(noCreditsViewState = NoCreditsAvailableViewState.noDataAvailable())
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
 * Represents the event that is triggered when the user selects a credit to see
 * the details of that item.
 */
data class NavigateToPersonEvent(
        val personId: String,
        val personImageUrl: String,
        val personName: String
)