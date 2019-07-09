package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDetailDAO
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.datasources.moviedetail.MovieDetailDb
import com.jpp.mpdomain.MovieDetail

/**
 * [MovieDetailDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class MovieDetailCache(private val roomDatabase: MPRoomDataBase,
                       private val adapter: RoomModelAdapter,
                       private val timestampHelper: CacheTimestampHelper) : MovieDetailDb {


    override fun getMovieDetails(movieId: Double): MovieDetail? {
        return withMovieDetailsDao {
            getMovieDetail(movieId, now())?.let { dbMovieDetails ->
                getGenresForDetailId(dbMovieDetails.id)?.let { dbGenres ->
                    transformWithAdapter { adaptDBMovieDetailToDataMovieDetail(dbMovieDetails, dbGenres) }
                }
            }
        }
    }

    override fun saveMovieDetails(movieDetail: MovieDetail) {
        withMovieDetailsDao {
            insertMovieDetail(transformWithAdapter { adaptDataMovieDetailToDBMovieDetail(movieDetail, movieDetailsRefreshTime()) })
            insertMovieGenres(movieDetail.genres.map { transformWithAdapter { adaptDataMovieGenreToDBMovieGenre(it, movieDetail.id) } })
        }
    }

    override fun flushData() {
        withMovieDetailsDao { deleteAll() }
    }

    private fun now() = timestampHelper.now()
    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }
    private fun <T> withMovieDetailsDao(action: MovieDetailDAO.() -> T): T = with(roomDatabase.movieDetailsDao()) { action.invoke(this) }
    private fun movieDetailsRefreshTime() = with(timestampHelper) { now() + movieDetailsRefreshTime() }
}