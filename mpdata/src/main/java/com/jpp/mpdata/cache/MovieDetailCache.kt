package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomDomainAdapter
import com.jpp.mpdata.datasources.moviedetail.MovieDetailDb
import com.jpp.mpdomain.MovieDetail

/**
 * [MovieDetailDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class MovieDetailCache(
    roomDatabase: MPRoomDataBase,
    private val toDomain: RoomDomainAdapter,
    private val toRoom: DomainRoomAdapter,
    private val timestamp: CacheTimestampHelper
) : MovieDetailDb {

    private val detailsDao = roomDatabase.movieDetailsDao()

    override fun getMovieDetails(movieId: Double): MovieDetail? {
        val dbMovieDetails = detailsDao.getMovieDetail(movieId, timestamp.now()) ?: return null
        val dbMovieGenres = detailsDao.getGenresForDetailId(dbMovieDetails.id) ?: return null
        return toDomain.movieDetail(dbMovieDetails, dbMovieGenres)
    }

    override fun saveMovieDetails(movieDetail: MovieDetail) {
        val dbMovieDetails = toRoom.movieDetail(movieDetail, timestamp.refreshTimestamp())
        val dbGenres = movieDetail.genres.map { toRoom.genre(it, movieDetail.id) }
        detailsDao.insertMovieDetail(dbMovieDetails)
        detailsDao.insertMovieGenres(dbGenres)
    }

    override fun flushData() {
        detailsDao.deleteAll()
    }

    /**
     * @return a Long that represents the expiration date of the movie details data stored in the
     * device.
     */
    private fun CacheTimestampHelper.refreshTimestamp(): Long = now() + movieDetailsRefreshTime()
}
