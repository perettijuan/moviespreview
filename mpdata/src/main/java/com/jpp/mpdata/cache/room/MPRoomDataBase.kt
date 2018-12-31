package com.jpp.mpdata.cache.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    (DBMoviePage::class),
    (DBMovie::class)
], version = 1)
abstract class MPRoomDataBase : RoomDatabase() {
    abstract fun moviesDao(): MovieDAO
}