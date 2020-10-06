package com.jpp.mp.main.discover.filters.genres

import com.jpp.mpdesign.mapped.MovieGenreItem

/**
 * Represents a filter item that wraps a movie genre.
 */
data class GenreFilterItem(
    val genreId: Int,
    val uiGenre: MovieGenreItem,
    val isSelected: Boolean
)