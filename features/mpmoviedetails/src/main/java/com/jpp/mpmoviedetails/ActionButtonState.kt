package com.jpp.mpmoviedetails



/**
 * Represents the state of the action buttons show to the user.
 */
internal data class ActionButtonState(
    val asClickable: Boolean = false,
    val imageRes: Int = R.drawable.ic_favorite_empty
) {
        fun favorite(): ActionButtonState = copy(
            asClickable = true,
            imageRes = R.drawable.ic_favorite_filled
        )

        fun noFavorite(): ActionButtonState = copy(
            asClickable = true,
            imageRes = R.drawable.ic_favorite_empty
        )

        fun watchList(): ActionButtonState = copy(
            asClickable = true,
            imageRes = R.drawable.ic_watchlist_filled
        )

        fun noWatchList(): ActionButtonState = copy(
            asClickable = true,
            imageRes = R.drawable.ic_watchlist_empty
        )
}
