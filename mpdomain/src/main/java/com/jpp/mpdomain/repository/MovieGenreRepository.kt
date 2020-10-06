package com.jpp.mpdomain.repository

import com.jpp.mpdomain.MovieGenre

/**
 * Repository layer to control movie [MovieGenre]
 */
interface MovieGenreRepository {

    /**
     * Retrieve the list of all [MovieGenre] available.
     */
    suspend fun getMovieGenres(): List<MovieGenre>?
}
