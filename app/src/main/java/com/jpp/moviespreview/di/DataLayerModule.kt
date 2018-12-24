package com.jpp.moviespreview.di

import android.content.Context
import com.jpp.moviespreview.BuildConfig
import com.jpp.moviespreview.datalayer.DataModelMapper
import com.jpp.moviespreview.datalayer.api.ServerRepository
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.MPDataBaseImpl
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestampsImpl
import com.jpp.moviespreview.datalayer.cache.repository.CacheConfigurationRepository
import com.jpp.moviespreview.datalayer.cache.repository.CacheMoviesRepository
import com.jpp.moviespreview.datalayer.cache.room.RoomModelAdapter
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepositoryImpl
import com.jpp.moviespreview.datalayer.repository.MoviesRepositoryImpl
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository
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
    fun providesDataMapper(): DataModelMapper = DataModelMapper()

    @Singleton
    @Provides
    fun providesConfigurationRepository(dbConfigurationRepository: CacheConfigurationRepository, serverRepositoryImpl: ServerRepository)
            : ConfigurationRepository = ConfigurationRepositoryImpl(dbConfigurationRepository, serverRepositoryImpl)

    @Singleton
    @Provides
    fun providesMoviesRepository(cacheMoviesRepository: CacheMoviesRepository, serverRepositoryImpl: ServerRepository)
            : MoviesRepository = MoviesRepositoryImpl(cacheMoviesRepository, serverRepositoryImpl)

    @Singleton
    @Provides
    fun providesCacheConfigurationRepository(mpCache: MPTimestamps, mpDataBase: MPDataBase, mapper: DataModelMapper)
            : CacheConfigurationRepository = CacheConfigurationRepository(mpCache, mpDataBase, mapper)

    @Singleton
    @Provides
    fun providesCacheMovieRepository(mpCache: MPTimestamps, mpDatabase: MPDataBase, mapper: DataModelMapper)
            : CacheMoviesRepository = CacheMoviesRepository(mpCache, mpDatabase, mapper)

    @Singleton
    @Provides
    fun providesServerRepositoryImpl(mapper: DataModelMapper) = ServerRepository(BuildConfig.API_KEY, mapper)

    @Singleton
    @Provides
    fun providesMPCache(context: Context): MPTimestamps = MPTimestampsImpl(context)

    @Singleton
    @Provides
    fun providesMPDataBase(context: Context): MPDataBase = MPDataBaseImpl(context, RoomModelAdapter())
}