package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDetailDAO
import com.jpp.mpdata.cache.room.RoomDomainAdapter
import com.jpp.mpdata.datasources.moviedetail.MovieDetailDb
import com.jpp.mpdomain.MovieDetail

/**
 * [MovieDetailDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class MovieDetailCache(
    private val roomDatabase: MPRoomDataBase,
    private val adapter: RoomDomainAdapter,
    private val timestampHelper: CacheTimestampHelper
) : MovieDetailDb {

    override fun getMovieDetails(movieId: Double): MovieDetail? {
        return withMovieDetailsDao {
            getMovieDetail(movieId, now())?.let { dbMovieDetails ->
                getGenresForDetailId(dbMovieDetails.id)?.let { dbGenres ->
                    adapter.adaptDBMovieDetailToDataMovieDetail(dbMovieDetails, dbGenres)
                }
            }
        }
    }

    override fun saveMovieDetails(movieDetail: MovieDetail) {
        withMovieDetailsDao {
            insertMovieDetail(adapter.adaptDataMovieDetailToDBMovieDetail(movieDetail, movieDetailsRefreshTime()))
            insertMovieGenres(movieDetail.genres.map { adapter.adaptDataMovieGenreToDBMovieGenre(it, movieDetail.id) })
        }
    }

    override fun flushData() {
        withMovieDetailsDao { deleteAll() }
    }



    /**
     * Helper function to execute an [action] with the [MovieDetailDAO] instance obtained from [MPRoomDataBase].
     */
    private fun <T> withMovieDetailsDao(action: MovieDetailDAO.() -> T): T = with(roomDatabase.movieDetailsDao()) { action.invoke(this) }

    /**
     * @return a Long that represents the current time.
     */
    private fun now() = timestampHelper.now()

    /**
     * @return a Long that represents the expiration date of the movie details data stored in the
     * device.
     */
    private fun movieDetailsRefreshTime() = with(timestampHelper) { now() + movieDetailsRefreshTime() }
}
