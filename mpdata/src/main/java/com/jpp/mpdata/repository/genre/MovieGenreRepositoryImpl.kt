
package com.jpp.mpdata.repository.genre

import com.jpp.mpdata.datasources.genre.MovieGenreApi
import com.jpp.mpdata.datasources.genre.MovieGenreDb
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.repository.MovieGenreRepository

/**
 * [MovieGenreRepository] implementation
 */
class MovieGenreRepositoryImpl(
    private val api: MovieGenreApi,
    val db: MovieGenreDb
) : MovieGenreRepository {

    override suspend fun getMovieGenres(): List<MovieGenre>? {
        return db.getMovieGenres() ?: api.getMovieGenres()?.also { genres ->
            db.saveMovieGenres(genres)
        }
    }
}