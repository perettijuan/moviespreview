package com.jpp.mpdata.cache.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO definition used to access the image sizes data stored locally using Room.
 */
@Dao
interface ImageSizeDAO {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insertImageSizes(sizes: List<DBImageSize>)

    @Query("select * from image_size where duedate >= :nowDate")
    fun getImageSizes(nowDate: Long): List<DBImageSize>
}