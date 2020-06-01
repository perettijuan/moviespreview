package com.jpp.mp.di

import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.usecase.ConfigureMovieImagesPathUseCase
import com.jpp.mpdomain.usecase.GetMoviePageUseCase
import dagger.Module
import dagger.Provides

/**
 * Provides all dependencies for the domain layer.
 */
@Module
class DomainLayerModule {

    @Provides
    fun providesImagesPathInteractor(configurationRepository: ConfigurationRepository):
            ImagesPathInteractor = ImagesPathInteractor.Impl(configurationRepository)


    @Provides
    fun providesConfigureMovieImagesPathUseCase(
            configurationRepository: ConfigurationRepository
    ): ConfigureMovieImagesPathUseCase = ConfigureMovieImagesPathUseCase(configurationRepository)

    @Provides
    fun providesGetMoviePageUseCase(
            moviePageRepository: MoviePageRepository,
            connectivityRepository: ConnectivityRepository,
            languageRepository: LanguageRepository
    ): GetMoviePageUseCase = GetMoviePageUseCase(moviePageRepository, connectivityRepository, languageRepository)

}
