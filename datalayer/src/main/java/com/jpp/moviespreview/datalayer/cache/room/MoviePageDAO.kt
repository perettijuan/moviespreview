package com.jpp.moviespreview.datalayer.cache.room

import androidx.room.*

@Dao
interface MoviePageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMoviePage(dbMoviePage: DBMoviePage)

    @Query("select * from movie_pages where _id = :page")
    fun getMoviePage(page: Int): DBMoviePage?

    @Query("DELETE FROM movie_pages")
    fun deleteAllPages()
}