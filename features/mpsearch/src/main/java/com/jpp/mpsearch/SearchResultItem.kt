package com.jpp.mpsearch

/**
 * Represents an item in the list of search results.
 */
internal data class SearchResultItem(
    val id: Double,
    val imagePath: String,
    val name: String,
    val icon: SearchResultTypeIcon
) {
    fun isMovieType() = icon == SearchResultTypeIcon.Movie
}
