package com.jpp.moviespreview.datalayer.cache.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    (DBImageSize::class),
    (DBMoviePage::class),
    (DBMovie::class)
], version = 1)
abstract class MPRoomDataBase : RoomDatabase() {
    abstract fun imageSizeDao(): ImageSizeDAO
    abstract fun moviePageDao(): MoviePageDAO
    abstract fun moviesDao() : MovieDAO
}