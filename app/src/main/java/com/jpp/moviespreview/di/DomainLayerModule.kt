package com.jpp.moviespreview.di

import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandlerImpl
import com.jpp.mpdomain.repository.*
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import com.jpp.mpdomain.usecase.person.GetPersonUseCase
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
    fun providesConfigurationHandler(): ConfigurationHandler = ConfigurationHandlerImpl()

    @Provides
    @Singleton
    fun providesNetworkExecutor(): Executor = NETWORK_IO

    @Provides
    @Singleton
    fun providesSearchUseCase(searchRepository: SearchRepository,
                              connectivityRepository: ConnectivityRepository)
            : SearchUseCase = SearchUseCase.Impl(searchRepository, connectivityRepository)

    @Provides
    @Singleton
    fun providesConfigSearchResultUseCase(configurationRepository: ConfigurationRepository,
                                          configurationHandler: ConfigurationHandler)
            : ConfigSearchResultUseCase = ConfigSearchResultUseCase.Impl(configurationRepository, configurationHandler)

    @Provides
    @Singleton
    fun providesGetMoviesUseCase(moviesRepository: MoviesRepository,
                                 connectivityRepository: ConnectivityRepository)
            : GetMoviesUseCase = GetMoviesUseCase.Impl(moviesRepository, connectivityRepository)

    @Provides
    @Singleton
    fun providesConfigMovieUseCase(configurationRepository: ConfigurationRepository,
                                   configurationHandler: ConfigurationHandler)
            : ConfigMovieUseCase = ConfigMovieUseCase.Impl(configurationRepository, configurationHandler)

    @Provides
    @Singleton
    fun providesGetMovieDetailsUseCase(moviesRepository: MoviesRepository,
                                       connectivityRepository: ConnectivityRepository)
            : GetMovieDetailsUseCase = GetMovieDetailsUseCase.Impl(moviesRepository, connectivityRepository)

    @Singleton
    @Provides
    fun providesGetPersonUseCase(personRepository: PersonRepository,
                                 connectivityRepository: ConnectivityRepository)
            : GetPersonUseCase = GetPersonUseCase.Impl(personRepository, connectivityRepository)
}