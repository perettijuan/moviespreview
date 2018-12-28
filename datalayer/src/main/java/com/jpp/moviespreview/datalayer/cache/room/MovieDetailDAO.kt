package com.jpp.moviespreview.datalayer.cache.room

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

    @Query("select * from movies_details where _id = :detailId")
    fun getMovieDetail(detailId: Double): DBMovieDetail?

    @Query("select * from genres where movie_detail_d = :movieId")
    fun getGenresForDetailId(movieId: Double): List<DBMovieGenre>?
}