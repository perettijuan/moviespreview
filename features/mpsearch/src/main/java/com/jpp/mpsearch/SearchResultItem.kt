package com.jpp.mpsearch

/**
 * Represents an item in the list of search results.
 */
data class SearchResultItem(
    val id: Double,
    val imagePath: String,
    val name: String,
    val icon: SearchResultTypeIcon
) {
    fun isMovieType() = icon == SearchResultTypeIcon.Movie
}