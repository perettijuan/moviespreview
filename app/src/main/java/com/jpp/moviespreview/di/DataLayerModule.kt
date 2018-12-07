package com.jpp.moviespreview.di

import android.content.Context
import com.jpp.moviespreview.BuildConfig
import com.jpp.moviespreview.datalayer.api.ServerRepository
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.MPDataBaseImpl
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestampsImpl
import com.jpp.moviespreview.datalayer.cache.repository.CacheConfigurationRepository
import com.jpp.moviespreview.datalayer.cache.room.RoomModelAdapter
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepositoryImpl
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
    fun provideConfigurationRepository(dbConfigurationRepository: CacheConfigurationRepository, serverRepositoryImpl: ServerRepository)
            : ConfigurationRepository = ConfigurationRepositoryImpl(dbConfigurationRepository, serverRepositoryImpl)

    @Singleton
    @Provides
    fun providesDBConfigurationRepository(mpCache: MPTimestamps, mpDataBase: MPDataBase)
            : CacheConfigurationRepository = CacheConfigurationRepository(mpCache, mpDataBase)

    @Singleton
    @Provides
    fun providesServerRepositoryImpl() = ServerRepository(BuildConfig.API_KEY)

    @Singleton
    @Provides
    fun providesMPCache(context: Context): MPTimestamps = MPTimestampsImpl(context)

    @Singleton
    @Provides
    fun providesMPDataBase(context: Context): MPDataBase = MPDataBaseImpl(context, RoomModelAdapter())
}