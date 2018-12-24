package com.jpp.moviespreview.datalayer.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDAO {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insertMovie(dbMovie: DBMovie)

    @Query("select * from movies where page_id = :pageId")
    fun getMoviesFromPage(pageId: Int) : List<DBMovie>?

    @Query("DELETE FROM movies")
    fun deleteAllMovies()
}