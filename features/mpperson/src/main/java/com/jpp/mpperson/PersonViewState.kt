package com.jpp.mpperson

import android.view.View
import com.jpp.mpdesign.views.MPErrorView.ErrorViewState

/**
 * Represents the view state of the profile person screen.
 */
internal data class PersonViewState(
    val screenTitle: String,
    val personImageUrl: String = "emptyUrl",
    val loadingVisibility: Int = View.INVISIBLE,
    val errorViewState: ErrorViewState = ErrorViewState.asNotVisible(),
    val contentViewState: PersonContentViewState = PersonContentViewState()
) {

    fun showUnknownError(errorHandler: () -> Unit): PersonViewState = copy(
        loadingVisibility = View.INVISIBLE,
        errorViewState = ErrorViewState.asUnknownError(errorHandler)
    )

    fun showNoConnectivityError(errorHandler: () -> Unit): PersonViewState =
        copy(
            loadingVisibility = View.INVISIBLE,
            errorViewState = ErrorViewState.asConnectivity(errorHandler)
        )

    fun showPerson(
        imageUrl: String,
        contentViewStateValue: PersonContentViewState
    ): PersonViewState = copy(
        loadingVisibility = View.INVISIBLE,
        personImageUrl = imageUrl,
        contentViewState = contentViewStateValue
    )

    fun showNoDataAvailable(imageUrl: String): PersonViewState = copy(
        loadingVisibility = View.INVISIBLE,
        personImageUrl = imageUrl,
        contentViewState = PersonContentViewState(
            dataAvailable = PersonRowViewState.noDataAvailableRow()
        )
    )

    companion object {
        fun showLoading(screenTitle: String, imageUrl: String) = PersonViewState(
            screenTitle = screenTitle,
            personImageUrl = imageUrl,
            loadingVisibility = View.VISIBLE
        )
    }
}
