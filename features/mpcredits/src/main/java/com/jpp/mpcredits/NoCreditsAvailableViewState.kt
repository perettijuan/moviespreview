package com.jpp.mpcredits

import android.view.View

/**
 * Represents the view state of the no credits available view.
 */
internal data class NoCreditsAvailableViewState(
    val visibility: Int = View.INVISIBLE,
    val titleRes: Int = R.string.no_credits_for_this_movie
) {
    fun noDataAvailable(): NoCreditsAvailableViewState = copy(visibility = View.VISIBLE)
}
