package com.jpp.moviespreview.datalayer.db.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    (DBImageSize::class)
], version = 1)
abstract class MPRoomDataBase : RoomDatabase() {
    abstract fun imageSizeDao(): ImageSizeDAO
}