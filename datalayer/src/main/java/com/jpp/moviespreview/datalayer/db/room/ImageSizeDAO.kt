package com.jpp.moviespreview.datalayer.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageSizeDAO {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insertImageSizes(sizes: List<DBImageSize>)

    @Query("select * from image_size")
    fun getImageSizes(): List<DBImageSize>?
}