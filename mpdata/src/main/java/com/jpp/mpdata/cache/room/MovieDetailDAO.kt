package com.jpp.mpdata.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO definition used to access the movie details data stored locally using Room.
 */
@Dao
interface MovieDetailDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieDetail(dbMovieDetail: DBMovieDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieGenres(genreByMovies: List<DBGenreByMovie>)

    @Query("select * from movies_details where _id = :detailId and duedate >= :nowDate")
    fun getMovieDetail(detailId: Double, nowDate: Long): DBMovieDetail?

    @Query("select * from genres where movie_detail_d = :movieId")
    fun getGenresForDetailId(movieId: Double): List<DBGenreByMovie>?

    @Query("DELETE FROM movies_details")
    fun deleteAll()
}
