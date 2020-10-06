package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.adapter.DomainRoomAdapter
import com.jpp.mpdata.cache.adapter.RoomDomainAdapter
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.datasources.genre.MovieGenreDb
import com.jpp.mpdomain.MovieGenre
/**
 * [MovieGenreDb] implementation with a caching mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class MovieGenreCache(
    roomDatabase: MPRoomDataBase,
    private val toDomain: RoomDomainAdapter,
    private val toRoom: DomainRoomAdapter,
    private val timestamp: CacheTimestampHelper
) : MovieGenreDb {

    private val movieGenresDao = roomDatabase.movieGenresDao()

    override fun getMovieGenres(): List<MovieGenre>? {
        val genresInDb = movieGenresDao.getMovieGenres(timestamp.now())

        if (genresInDb == null || genresInDb.isEmpty()) {
            return null
        }

        return genresInDb.let { dbGenres ->
            dbGenres.map { genre -> toDomain.movieGenre(genre) }
        }
    }

    override fun saveMovieGenres(genres: List<MovieGenre>) {
        val dbGenres = genres.map { domainGenre ->
            toRoom.movieGenre(domainGenre, timestamp.movieGenreDueDate())
        }
        movieGenresDao.saveMovieGenres(dbGenres)
    }

    /**
     * @return a Long that represents the expiration date of the data stored in the device.
     */
    private fun CacheTimestampHelper.movieGenreDueDate() = now() + movieGenresRefreshTime()
}