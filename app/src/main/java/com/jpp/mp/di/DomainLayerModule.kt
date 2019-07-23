package com.jpp.mp.di

import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpdomain.repository.*
import com.jpp.mpdomain.usecase.licenses.GetLicenseUseCase
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
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

    @Provides
    @Singleton
    fun providesNetworkExecutor(): Executor = Executors.newFixedThreadPool(5)

    @Provides
    fun providesGetMoviesUseCase(moviePageRepository: MoviePageRepository,
                                 connectivityRepository: ConnectivityRepository,
                                 languageRepository: LanguageRepository)
            : GetMoviesUseCase = GetMoviesUseCase.Impl(moviePageRepository, connectivityRepository, languageRepository)

    @Provides
    fun providesConfigMovieUseCase(configurationRepository: ConfigurationRepository)
            : ConfigMovieUseCase = ConfigMovieUseCase.Impl(configurationRepository)

    @Provides
    fun providesGetLicenseUseCase(licensesRepository: LicensesRepository)
            : GetLicenseUseCase = GetLicenseUseCase.Impl(licensesRepository)

    @Provides
    fun providesImagesPathInteractor(configurationRepository: ConfigurationRepository)
            : ImagesPathInteractor = ImagesPathInteractor.Impl(configurationRepository)

}