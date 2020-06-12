package com.jpp.mpcredits

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the credits screen.
 */
internal data class CreditsViewState(
    val loadingVisibility: Int = View.INVISIBLE,
    val screenTitle: String,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val creditsViewState: CreditsContentViewState = CreditsContentViewState(),
    val noCreditsViewState: NoCreditsAvailableViewState = NoCreditsAvailableViewState()
) {

    fun showUnknownError(errorHandler: () -> Unit): CreditsViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )

    fun showNoConnectivityError(errorHandler: () -> Unit): CreditsViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )

    fun showCredits(creditItems: List<CreditPerson>): CreditsViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            creditsViewState = creditsViewState.creditList(creditItems)
        )

    fun showNoCreditsAvailable(): CreditsViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            noCreditsViewState = noCreditsViewState.noDataAvailable()
        )

    companion object {
        fun showLoading(screenTitle: String) =
            CreditsViewState(loadingVisibility = View.VISIBLE, screenTitle = screenTitle)
    }
}
