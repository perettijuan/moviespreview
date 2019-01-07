package com.jpp.moviespreview.di

import android.content.Context
import androidx.room.Room
import com.jpp.mpdata.api.MPApi
import com.jpp.mpdata.cache.CacheTimestampHelper
import com.jpp.mpdata.cache.ConfigurationCache
import com.jpp.mpdata.cache.MoviesCache
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.cache.timestamps.MPTimestamps
import com.jpp.mpdata.cache.timestamps.MPTimestampsImpl
import com.jpp.mpdomain.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.configuration.ConfigurationDb
import com.jpp.mpdomain.repository.movies.MoviesApi
import com.jpp.mpdomain.repository.movies.MoviesDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides all dependencies for the data layer.
 */
@Module
class DataLayerModule {

    /*****************************************
     ****** COMMON INNER DEPENDENCIES ********
     *****************************************/


    @Singleton
    @Provides
    fun providesMPApi() = MPApi()

    @Singleton
    @Provides
    fun providesMPTimestamps(context: Context): MPTimestamps = MPTimestampsImpl(context)

    @Provides
    @Singleton
    fun providesTheMoviesDBRoomDb(context: Context)
            : MPRoomDataBase = Room
            .databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
            .build()

    @Singleton
    @Provides
    fun providesRoomModelAdapter() = RoomModelAdapter()

    @Singleton
    @Provides
    fun providesCacheTimestampHelper() = CacheTimestampHelper()


    /***********************************
     ****** MOVIES DEPENDENCIES ********
     ***********************************/

    @Singleton
    @Provides
    fun providesMoviesApi(mpApiInstance: MPApi): MoviesApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMoviesDb(roomDb: MPRoomDataBase,
                         adapter: RoomModelAdapter,
                         timestampHelper: CacheTimestampHelper): MoviesDb = MoviesCache(roomDb, adapter, timestampHelper)

    /*****************************************
     ****** CONFIGURATION DEPENDENCIES *******
     *****************************************/

    @Singleton
    @Provides
    fun providesConfigurationApi(mpApiInstance: MPApi): ConfigurationApi = mpApiInstance

    @Singleton
    @Provides
    fun providesConfigurationDb(mpTimestamps: MPTimestamps,
                                roomDb: MPRoomDataBase,
                                adapter: RoomModelAdapter): ConfigurationDb = ConfigurationCache(mpTimestamps, roomDb, adapter)
}