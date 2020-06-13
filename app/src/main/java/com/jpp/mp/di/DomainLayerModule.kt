package com.jpp.mp.di

import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpdomain.repository.*
import com.jpp.mpdomain.usecase.*
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
    ): GetMoviePageUseCase =
        GetMoviePageUseCase(moviePageRepository, connectivityRepository, languageRepository)

    @Provides
    fun providesGetMovieDetailUseCase(
        movieDetailRepository: MovieDetailRepository,
        connectivityRepository: ConnectivityRepository,
        languageRepository: LanguageRepository
    ): GetMovieDetailUseCase =
        GetMovieDetailUseCase(movieDetailRepository, connectivityRepository, languageRepository)

    @Provides
    fun providesGetMovieStateUseCase(
        sessionRepository: SessionRepository,
        movieStateRepository: MovieStateRepository,
        connectivityRepository: ConnectivityRepository
    ): GetMovieStateUseCase =
        GetMovieStateUseCase(sessionRepository, movieStateRepository, connectivityRepository)

    @Provides
    fun providesUpdateFavoriteMovieStateUseCase(
        movieStateRepository: MovieStateRepository,
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        connectivityRepository: ConnectivityRepository
    ): UpdateFavoriteMovieStateUseCase = UpdateFavoriteMovieStateUseCase(
        movieStateRepository,
        moviePageRepository,
        sessionRepository,
        accountRepository,
        connectivityRepository
    )

    @Provides
    fun providesUpdateWatchlistMovieStateUseCase(
        movieStateRepository: MovieStateRepository,
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        connectivityRepository: ConnectivityRepository
    ): UpdateWatchlistMovieStateUseCase = UpdateWatchlistMovieStateUseCase(
        movieStateRepository,
        moviePageRepository,
        sessionRepository,
        accountRepository,
        connectivityRepository
    )

    @Provides
    fun providesRateMovieUseCase(
        movieStateRepository: MovieStateRepository,
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        connectivityRepository: ConnectivityRepository
    ): RateMovieUseCase = RateMovieUseCase(
        sessionRepository,
        moviePageRepository,
        movieStateRepository,
        accountRepository,
        connectivityRepository
    )

    @Provides
    fun providesDeleteMovieRatingUseCase(
        movieStateRepository: MovieStateRepository,
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        connectivityRepository: ConnectivityRepository
    ): DeleteMovieRatingUseCase = DeleteMovieRatingUseCase(
        sessionRepository,
        moviePageRepository,
        movieStateRepository,
        connectivityRepository
    )

    @Provides
    fun providesGetCreditsUseCase(
        creditsRepository: CreditsRepository,
        configurationRepository: ConfigurationRepository,
        connectivityRepository: ConnectivityRepository
    ): GetCreditsUseCase =
        GetCreditsUseCase(creditsRepository, configurationRepository, connectivityRepository)

    @Provides
    fun providesGetPersonUseCase(
        personRepository: PersonRepository,
        connectivityRepository: ConnectivityRepository,
        languageRepository: LanguageRepository
    ): GetPersonUseCase =
        GetPersonUseCase(personRepository, connectivityRepository, languageRepository)
}
