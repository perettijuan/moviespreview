package com.jpp.moviespreview.di

import android.content.Context
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandlerImpl
import com.jpp.mpdomain.repository.ConfigurationApi
import com.jpp.mpdomain.repository.ConfigurationDb
import com.jpp.mpdomain.repository.movies.MovieListRepository
import com.jpp.mpdomain.repository.movies.MovieListRepositoryImpl
import com.jpp.mpdomain.repository.MoviesApi
import com.jpp.mpdomain.repository.MoviesDb
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

/**
 * Provides all dependencies for the domain layer.
 */
@Module
class DomainLayerModule {

    private val NETWORK_IO = Executors.newFixedThreadPool(5)

    @Provides
    @Singleton
    fun providesConnectivityHandler(context: Context): ConnectivityHandler = ConnectivityHandler.ConnectivityHandlerImpl(context)

    @Provides
    @Singleton
    fun providesConfigurationHandler(): ConfigurationHandler = ConfigurationHandlerImpl()

    @Provides
    @Singleton
    fun providesNetworkExecutor(): Executor = NETWORK_IO

    @Provides
    @Singleton
    fun providesMovieListRepository(moviesApi: MoviesApi,
                                    moviesDb: MoviesDb,
                                    configurationApi: ConfigurationApi,
                                    configurationDb: ConfigurationDb,
                                    connectivityHandler: ConnectivityHandler,
                                    configurationHandler: ConfigurationHandler,
                                    networkExecutor: Executor) : MovieListRepository
            = MovieListRepositoryImpl(moviesApi, moviesDb, configurationApi, configurationDb, connectivityHandler, configurationHandler, networkExecutor)
}