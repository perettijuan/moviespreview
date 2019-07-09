package com.jpp.mpdata.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDetailDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieDetail(dbMovieDetail: DBMovieDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieGenres(movieGenres: List<DBMovieGenre>)

    @Query("select * from movies_details where _id = :detailId and duedate >= :nowDate")
    fun getMovieDetail(detailId: Double, nowDate: Long): DBMovieDetail?

    @Query("select * from genres where movie_detail_d = :movieId")
    fun getGenresForDetailId(movieId: Double): List<DBMovieGenre>?

    @Query("DELETE FROM movies_details")
    fun deleteAll()
}