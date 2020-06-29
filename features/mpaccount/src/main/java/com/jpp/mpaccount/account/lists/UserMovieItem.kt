package com.jpp.mpaccount.account.lists

/**
 * Represents an item in the list of User Movies.
 */
internal data class UserMovieItem(
    val movieId: Double,
    val headerImageUrl: String,
    val title: String,
    val contentImageUrl: String
)
