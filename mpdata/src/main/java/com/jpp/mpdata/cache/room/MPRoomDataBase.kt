package com.jpp.mpdata.cache.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * [RoomDatabase] definition. This class defines all the tables that can be accessed when
 * using the database.
 */
@Database(entities = [
    (DBMoviePage::class),
    (DBMovie::class),
    (DBImageSize::class),
    (DBMovieDetail::class),
    (DBMovieGenre::class),
    (DBCastCharacter::class),
    (DBCrewPerson::class)
], version = 1)
abstract class MPRoomDataBase : RoomDatabase() {
    abstract fun imageSizeDao() : ImageSizeDAO
    abstract fun moviesDao(): MovieDAO
    abstract fun movieDetailsDao(): MovieDetailDAO
    abstract fun creditsDao(): CreditsDao
}