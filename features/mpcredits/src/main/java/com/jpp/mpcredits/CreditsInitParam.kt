package com.jpp.mpcredits

import com.jpp.mp.common.extensions.getResIdFromAttribute

/**
 * The initialization parameter used for
 * CreditsViewModel initialization.
 */
data class CreditsInitParam(
    val movieTitle: String,
    val movieId: Double,
    val targetImageSize: Int
) {
    companion object {
        fun create(fragment: CreditsFragment) =
            CreditsInitParam(
                movieTitle = NavigationCredits.movieTitle(fragment.arguments),
                movieId = NavigationCredits.movieId(fragment.arguments),
                targetImageSize = fragment.resources.getDimensionPixelSize(
                    fragment.getResIdFromAttribute(
                        R.attr.mpCreditItemImageSize
                    )
                )
            )
    }
}