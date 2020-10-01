package com.jpp.mp.main.discover

/**
 * Represents an item in the list of discovered movies.
 */
data class DiscoveredMovieListItem(
    val movieId: Double,
    val headerImageUrl: String,
    val title: String,
    val contentImageUrl: String,
    val popularity: String,
    val voteCount: String
)