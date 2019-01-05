package com.jpp.mpdata

import android.content.Context
import androidx.room.Room
import com.jpp.mpdata.api.MPApi
import com.jpp.mpdata.cache.ConfigurationCache
import com.jpp.mpdata.cache.MoviesCache
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.cache.timestamps.MPTimestamps
import com.jpp.mpdata.cache.timestamps.MPTimestampsImpl
import com.jpp.mpdomain.repository.ConfigurationApi
import com.jpp.mpdomain.repository.ConfigurationDb
import com.jpp.mpdomain.repository.MoviesApi
import com.jpp.mpdomain.repository.MoviesDb
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

    private lateinit var roomDataBase: MPRoomDataBase

    @Singleton
    @Provides
    fun providesMPApi() = MPApi()

    @Singleton
    @Provides
    fun providesMPTimestamps(context: Context): MPTimestamps = MPTimestampsImpl(context)


    private fun getRoomDb(context: Context): MPRoomDataBase {
        if(!::roomDataBase.isInitialized) {
            roomDataBase =  Room
                    .databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
                    .build()
        }
        return roomDataBase
    }

    @Singleton
    @Provides
    fun providesRoomModelAdapter() = RoomModelAdapter()


    /***********************************
     ****** MOVIES DEPENDENCIES ********
     ***********************************/

    @Singleton
    @Provides
    fun providesMoviesApi(mpApiInstance: MPApi): MoviesApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMoviesDb(context: Context,
                         mpTimestamps: MPTimestamps,
                         adapter: RoomModelAdapter): MoviesDb = MoviesCache(context, mpTimestamps, getRoomDb(context), adapter)

    /*****************************************
     ****** CONFIGURATION DEPENDENCIES *******
     *****************************************/

    @Singleton
    @Provides
    fun providesConfigurationApi(mpApiInstance: MPApi): ConfigurationApi = mpApiInstance

    @Singleton
    @Provides
    fun providesConfigurationDb(context: Context,
                                mpTimestamps: MPTimestamps,
                                adapter: RoomModelAdapter): ConfigurationDb = ConfigurationCache(mpTimestamps, getRoomDb(context), adapter)
}