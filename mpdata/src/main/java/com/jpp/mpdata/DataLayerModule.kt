package com.jpp.mpdata

import android.content.Context
import androidx.room.Room
import com.jpp.mpdata.api.MPApi
import com.jpp.mpdata.cache.MPCache
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.cache.timestamps.MPTimestampsImpl
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


    @Singleton
    @Provides
    fun providesMPApi() = MPApi()

    @Singleton
    @Provides
    fun providesMoviesApi(mpApiInstance: MPApi): MoviesApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMPCache(context: Context): MPCache {
        val mpTimestamps = MPTimestampsImpl(context)
        val roomDB = Room
                .databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
                .build()
        val adapter = RoomModelAdapter()
        return MPCache(context, mpTimestamps, roomDB, adapter)
    }

    @Singleton
    @Provides
    fun providesMoviesDb(mpCache: MPCache): MoviesDb = mpCache
}