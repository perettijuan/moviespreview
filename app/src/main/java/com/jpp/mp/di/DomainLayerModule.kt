package com.jpp.mp.di

import com.jpp.mpdomain.repository.AccessTokenRepository
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.LicensesRepository
import com.jpp.mpdomain.repository.MovieDetailRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mpdomain.repository.MovieStateRepository
import com.jpp.mpdomain.repository.PersonRepository
import com.jpp.mpdomain.repository.SearchRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.DeleteMovieRatingUseCase
import com.jpp.mpdomain.usecase.FindAppLicenseUseCase
import com.jpp.mpdomain.usecase.GetAccessTokenUseCase
import com.jpp.mpdomain.usecase.GetAppLicensesUseCase
import com.jpp.mpdomain.usecase.GetCreditsUseCase
import com.jpp.mpdomain.usecase.GetMovieDetailUseCase
import com.jpp.mpdomain.usecase.GetMoviePageUseCase
import com.jpp.mpdomain.usecase.GetMovieStateUseCase
import com.jpp.mpdomain.usecase.GetPersonUseCase
import com.jpp.mpdomain.usecase.GetUserAccountMoviePageUseCase
import com.jpp.mpdomain.usecase.GetUserAccountMoviesUseCase
import com.jpp.mpdomain.usecase.GetUserAccountUseCase
import com.jpp.mpdomain.usecase.LogOutUseCase
import com.jpp.mpdomain.usecase.LoginUseCase
import com.jpp.mpdomain.usecase.RateMovieUseCase
import com.jpp.mpdomain.usecase.SearchUseCase
import com.jpp.mpdomain.usecase.UpdateFavoriteMovieStateUseCase
import com.jpp.mpdomain.usecase.UpdateWatchlistMovieStateUseCase
import dagger.Module
import dagger.Provides

/**
 * Provides all dependencies for the domain layer.
 */
@Module
class DomainLayerModule {

    @Provides
    fun providesGetMoviePageUseCase(
        moviePageRepository: MoviePageRepository,
        configurationRepository: ConfigurationRepository,
        connectivityRepository: ConnectivityRepository,
        languageRepository: LanguageRepository
    ): GetMoviePageUseCase =
        GetMoviePageUseCase(
            moviePageRepository,
            configurationRepository,
            connectivityRepository,
            languageRepository
        )

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
    fun providesGetUserAccountMoviePageUseCase(
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        configurationRepository: ConfigurationRepository,
        languageRepository: LanguageRepository,
        connectivityRepository: ConnectivityRepository
    ): GetUserAccountMoviePageUseCase = GetUserAccountMoviePageUseCase(
        moviePageRepository,
        sessionRepository,
        accountRepository,
        configurationRepository,
        languageRepository,
        connectivityRepository
    )

    @Provides
    fun providesGetUserAccountMoviesUseCase(
        moviePageRepository: MoviePageRepository,
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        configurationRepository: ConfigurationRepository,
        languageRepository: LanguageRepository,
        connectivityRepository: ConnectivityRepository
    ): GetUserAccountMoviesUseCase = GetUserAccountMoviesUseCase(
        moviePageRepository,
        sessionRepository,
        accountRepository,
        configurationRepository,
        languageRepository,
        connectivityRepository
    )

    @Provides
    fun providesLogOutUseCase(
        sessionRepository: SessionRepository,
        accountRepository: AccountRepository,
        moviePageRepository: MoviePageRepository
    ): LogOutUseCase = LogOutUseCase(sessionRepository, accountRepository, moviePageRepository)
}
