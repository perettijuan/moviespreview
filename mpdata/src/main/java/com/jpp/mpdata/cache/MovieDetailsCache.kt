package com.jpp.mpdata.cache

import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.MovieDetailDAO
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.repository.details.MovieDetailsDb

/**
 * [MovieDetailsDb] implementation with a cache mechanism to verify that the data stored in the application
 * is valid after a period of time.
 */
class MovieDetailsCache(private val roomDatabase: MPRoomDataBase,
                        private val adapter: RoomModelAdapter,
                        private val timestampHelper: CacheTimestampHelper) : MovieDetailsDb {


    override fun getMovieDetails(movieId: Double): MovieDetail? {
        return withDao {
            getMovieDetail(movieId, now())?.let { dbMovieDetails ->
                getGenresForDetailId(dbMovieDetails.id)?.let { dbGenres ->
                    transformWithAdapter { adaptDBMovieDetailToDataMovieDetail(dbMovieDetails, dbGenres) }
                }
            }
        }
    }

    override fun saveMovieDetails(movieDetail: MovieDetail) {
        withDao {
            insertMovieDetail(transformWithAdapter { adaptDataMovieDetailToDBMovieDetail(movieDetail, movieDetailsRefreshTime()) })
            insertMovieGenres(movieDetail.genres.map { transformWithAdapter { adaptDataMovieGenreToDBMovieGenre(it, movieDetail.id) } })
        }
    }


    private fun <T> transformWithAdapter(action: RoomModelAdapter.() -> T): T = with(adapter) { action.invoke(this) }

    private fun <T> withDao(action: MovieDetailDAO.() -> T): T = with(roomDatabase.movieDetailsDao()) { action.invoke(this) }

    private fun now() = timestampHelper.now()

    private fun movieDetailsRefreshTime() = with(timestampHelper) { now() + movieDetailsRefreshTime() }
}
