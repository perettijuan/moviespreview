package com.jpp.mpdata.datasources.genre

import com.jpp.mpdomain.MovieGenre

/**
 * Database definition for [MovieGenre].
 */
interface MovieGenreDb {
    /**
     * Retrieves the list of all [MovieGenre] available or null
     * if an error is detected.
     */
    fun getMovieGenres(): List<MovieGenre>?

    /**
     * Stores the provided list of [MovieGenre].
     */
    fun saveMovieGenres(genres: List<MovieGenre>)
}
