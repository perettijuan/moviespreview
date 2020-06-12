package com.jpp.mpperson

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the profile person screen.
 */
data class PersonViewState(
    val screenTitle: String,
    val personImageUrl: String = "emptyUrl",
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: PersonContentViewState = PersonContentViewState()
) {

    companion object {
        fun showLoading(screenTitle: String, imageUrl: String) = PersonViewState(
            screenTitle = screenTitle,
            personImageUrl = imageUrl,
            loadingVisibility = View.VISIBLE
        )

        fun showUnknownError(screenTitle: String, errorHandler: () -> Unit) = PersonViewState(
            screenTitle = screenTitle,
            errorViewState = ErrorViewState.asUnknownError(errorHandler)
        )

        fun showNoConnectivityError(screenTitle: String, errorHandler: () -> Unit) =
            PersonViewState(
                screenTitle = screenTitle,
                errorViewState = ErrorViewState.asConnectivity(errorHandler)
            )

        fun showPerson(
            screenTitle: String,
            imageUrl: String,
            contentViewStateValue: PersonContentViewState
        ) = PersonViewState(
            screenTitle = screenTitle,
            personImageUrl = imageUrl,
            contentViewState = contentViewStateValue
        )

        fun showNoDataAvailable(screenTitle: String, imageUrl: String) = PersonViewState(
            screenTitle = screenTitle,
            personImageUrl = imageUrl,
            contentViewState = PersonContentViewState(
                dataAvailable = PersonRowViewState.noDataAvailableRow()
            )
        )
    }
}