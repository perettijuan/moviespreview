package com.jpp.mpsearch

import android.view.View

/**
 * Represents the view state of the search placeholder view.
 */
internal data class SearchPlaceHolderViewState(
    val visibility: Int = View.INVISIBLE,
    val icon: Int = R.drawable.ic_app_icon_black
)
