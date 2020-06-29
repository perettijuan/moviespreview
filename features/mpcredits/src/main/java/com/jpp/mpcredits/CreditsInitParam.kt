package com.jpp.mpcredits

/**
 * The initialization parameter used for
 * CreditsViewModel initialization.
 */
internal data class CreditsInitParam(
    val movieTitle: String,
    val movieId: Double
) {
    companion object {
        fun create(fragment: CreditsFragment) =
            CreditsInitParam(
                movieTitle = NavigationCredits.movieTitle(fragment.arguments),
                movieId = NavigationCredits.movieId(fragment.arguments)
            )
    }
}
