package com.jpp.moviespreview.di

import android.content.Context
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandlerImpl
import com.jpp.mpdomain.repository.MoviesRepository
import com.jpp.mpdomain.repository.SearchRepository
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import com.jpp.mpdomain.usecase.search.ConfigSearchResultUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCase
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
    fun providesSearchUseCase(searchRepository: SearchRepository,
                              connectivityHandler: ConnectivityHandler)
            : SearchUseCase = SearchUseCase.Impl(searchRepository, connectivityHandler)

    @Provides
    @Singleton
    fun providesConfigSearchResultUseCase(configurationRepository: ConfigurationRepository,
                                          configurationHandler: ConfigurationHandler)
            : ConfigSearchResultUseCase = ConfigSearchResultUseCase.Impl(configurationRepository, configurationHandler)

    @Provides
    @Singleton
    fun providesGetMoviesUseCase(moviesRepository: MoviesRepository,
                                 connectivityHandler: ConnectivityHandler)
            : GetMoviesUseCase = GetMoviesUseCase.Impl(moviesRepository, connectivityHandler)

    @Provides
    @Singleton
    fun providesConfigMovieUseCase(configurationRepository: ConfigurationRepository,
                                   configurationHandler: ConfigurationHandler)
            : ConfigMovieUseCase = ConfigMovieUseCase.Impl(configurationRepository, configurationHandler)

    @Provides
    @Singleton
    fun providesGetMovieDetailsUseCase(moviesRepository: MoviesRepository,
                                       connectivityHandler: ConnectivityHandler)
            : GetMovieDetailsUseCase = GetMovieDetailsUseCase.Impl(moviesRepository, connectivityHandler)
}