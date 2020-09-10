package com.jpp.mp.main.movies

/**
 * Represents an item in the list of Movies.
 */
data class MovieListItem(
    val movieId: Double,
    val headerImageUrl: String,
    val title: String,
    val contentImageUrl: String,
    val popularity: String,
    val voteCount: String
)
