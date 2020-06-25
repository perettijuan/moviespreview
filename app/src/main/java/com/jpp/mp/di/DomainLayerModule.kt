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

    @Provides
    fun providesSearchUseCase(
        searchRepository: SearchRepository,
        configurationRepository: ConfigurationRepository,
        connectivityRepository: ConnectivityRepository,
        languageRepository: LanguageRepository
    ): SearchUseCase = SearchUseCase(
        searchRepository,
        configurationRepository,
        connectivityRepository,
        languageRepository
    )

    @Provides
    fun providesGetAppLicensesUseCase(licensesRepository: LicensesRepository): GetAppLicensesUseCase =
        GetAppLicensesUseCase(licensesRepository)

    @Provides
    fun providesFindAppLicenseUseCase(licensesRepository: LicensesRepository): FindAppLicenseUseCase =
        FindAppLicenseUseCase(licensesRepository)

    @Provides
    fun providesGetUserAccountUseCase(
        accountRepository: AccountRepository,
        sessionRepository: SessionRepository,
        connectivityRepository: ConnectivityRepository
    ): GetUserAccountUseCase = GetUserAccountUseCase(
        accountRepository, sessionRepository, connectivityRepository
    )

    @Provides
    fun providesGetAccessTokenUseCase(
        accessTokenRepository: AccessTokenRepository,
        connectivityRepository: ConnectivityRepository
    ): GetAccessTokenUseCase = GetAccessTokenUseCase(accessTokenRepository, connectivityRepository)

    @Provides
    fun providesLoginUseCase(
        sessionRepository: SessionRepository,
        connectivityRepository: ConnectivityRepository
    ): LoginUseCase = LoginUseCase(
        sessionRepository, connectivityRepository
    )

    @Provides
    fun providesGetFavoriteMoviePageUseCase(
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        configurationRepository: ConfigurationRepository,
        languageRepository: LanguageRepository,
        connectivityRepository: ConnectivityRepository
    ): GetFavoriteMoviePageUseCase = GetFavoriteMoviePageUseCase(
        moviePageRepository,
        sessionRepository,
        accountRepository,
        configurationRepository,
        languageRepository,
        connectivityRepository
    )

    @Provides
    fun providesGetRatedMoviesUseCase(
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        configurationRepository: ConfigurationRepository,
        languageRepository: LanguageRepository,
        connectivityRepository: ConnectivityRepository
    ): GetRatedMoviesUseCase = GetRatedMoviesUseCase(
        moviePageRepository,
        sessionRepository,
        accountRepository,
        configurationRepository,
        languageRepository,
        connectivityRepository
    )

    @Provides
    fun providesGetWatchListMoviePage(
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        configurationRepository: ConfigurationRepository,
        languageRepository: LanguageRepository,
        connectivityRepository: ConnectivityRepository
    ): GetWatchListMoviePage = GetWatchListMoviePage(
        moviePageRepository,
        sessionRepository,
        accountRepository,
        configurationRepository,
        languageRepository,
        connectivityRepository
    )
}
