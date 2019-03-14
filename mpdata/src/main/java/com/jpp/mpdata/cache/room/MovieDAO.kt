package com.jpp.mpdata.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMoviePage(dbMoviePage: DBMoviePage): Long

    @Query("select * from movie_pages where page = :page and section = :section and duedate >= :nowDate and language = :lang")
    fun getMoviePage(page: Int, section: String, nowDate: Long, lang: String): DBMoviePage?

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insertMovies(dbMovies: List<DBMovie>)

    @Query("select * from movies where page_id = :pageId")
    fun getMoviesFromPage(pageId: Int) : List<DBMovie>?
}