package com.jpp.mpdata.datasources.genre

import com.jpp.mpdomain.MovieGenre

/**
 * API definition for [MovieGenre].
 */
interface MovieGenreApi {

    /**
     * Retrieves the list of all [MovieGenre] available or null
     * if an error is detected.
     */
    fun getMovieGenres(): List<MovieGenre>?
}