package com.jpp.mpdata.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMoviePage(dbMoviePage: DBMoviePage)

    @Query("select * from movie_pages where _id = :page")
    fun getMoviePage(page: Int): DBMoviePage?

    @Query("DELETE FROM movie_pages")
    fun deleteAllPages()

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insertMovies(dbMovies: List<DBMovie>)

    @Query("select * from movies where page_id = :pageId")
    fun getMoviesFromPage(pageId: Int) : List<DBMovie>?

    @Query("DELETE FROM movies")
    fun deleteAllMovies()
}