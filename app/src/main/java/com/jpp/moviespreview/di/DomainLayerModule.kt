package com.jpp.moviespreview.di

import android.content.Context
import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.ConnectivityVerifierImpl
import com.jpp.moviespreview.domainlayer.interactor.ConfigureMovieImages
import com.jpp.moviespreview.domainlayer.interactor.GetConfiguredMoviePage
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePage
import com.jpp.moviespreview.domainlayer.interactor.movie.ConfigureMovieImagesImpl
import com.jpp.moviespreview.domainlayer.interactor.movie.GetConfiguredMoviePageImpl
import com.jpp.moviespreview.domainlayer.interactor.movie.GetMoviePageImpl
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository
import com.jpp.moviespreview.domainlayer.repository.MovieListRepository
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository
import com.jpp.moviespreview.domainlayer.repository.movies.MovieListRepositoryImpl
import com.jpp.moviespreview.domainlayer.repository.movies.MoviesPagingDataSourceFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides all dependencies for the domain layer.
 */
@Module
class DomainLayerModule {

    @Provides
    @Singleton
    fun providesConnectivityVerifier(context: Context): ConnectivityVerifier = ConnectivityVerifierImpl(context)

    @Provides
    @Singleton
    fun providesGetMoviePage(moviesRepository: MoviesRepository, connectivityVerifier: ConnectivityVerifier)
            : GetMoviePage = GetMoviePageImpl(moviesRepository, connectivityVerifier)

    @Provides
    @Singleton
    fun providesConfigureMovieImages(configRepository: ConfigurationRepository)
            : ConfigureMovieImages = ConfigureMovieImagesImpl(configRepository)

    @Provides
    @Singleton
    fun providesGetConfiguredMoviePage(getMoviePage: GetMoviePage, configureMovieImages: ConfigureMovieImages)
            : GetConfiguredMoviePage = GetConfiguredMoviePageImpl(getMoviePage, configureMovieImages)

    @Provides
    @Singleton
    fun providesMovieListRepository(moviePage: GetConfiguredMoviePage): MovieListRepository =
        MovieListRepositoryImpl(dataSourceFactory = MoviesPagingDataSourceFactory(moviePage))

}