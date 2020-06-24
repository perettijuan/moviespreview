package com.jpp.mpaccount.account
/**
 * Represents an item shown in the user movies section.
 */
internal data class UserMovieItem(val image: String) : UserAccountMoviesView.UserAccountMovieItem {
    override fun getImageUrl(): String = image
}
