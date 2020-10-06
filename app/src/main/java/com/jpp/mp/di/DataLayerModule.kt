package com.jpp.mp.di

import android.content.Context
import androidx.room.Room
import com.jpp.mpdata.api.MPApi
import com.jpp.mpdata.cache.CacheTimestampHelper
import com.jpp.mpdata.cache.ConfigurationCache
import com.jpp.mpdata.cache.CreditsCache
import com.jpp.mpdata.cache.MovieDetailCache
import com.jpp.mpdata.cache.MoviesCache
import com.jpp.mpdata.cache.SupportCache
import com.jpp.mpdata.cache.MovieGenreCache
import com.jpp.mpdata.cache.adapter.DomainRoomAdapter
import com.jpp.mpdata.cache.adapter.RoomDomainAdapter
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdata.datasources.configuration.ConfigurationApi
import com.jpp.mpdata.datasources.configuration.ConfigurationDb
import com.jpp.mpdata.datasources.credits.CreditsApi
import com.jpp.mpdata.datasources.credits.CreditsDb
import com.jpp.mpdata.datasources.genre.MovieGenreApi
import com.jpp.mpdata.datasources.genre.MovieGenreDb
import com.jpp.mpdata.datasources.language.LanguageDb
import com.jpp.mpdata.datasources.language.LanguageMonitor
import com.jpp.mpdata.datasources.moviedetail.MovieDetailApi
import com.jpp.mpdata.datasources.moviedetail.MovieDetailDb
import com.jpp.mpdata.datasources.moviepage.MoviePageApi
import com.jpp.mpdata.datasources.moviepage.MoviePageDb
import com.jpp.mpdata.datasources.moviestate.MovieStateApi
import com.jpp.mpdata.datasources.person.PersonApi
import com.jpp.mpdata.datasources.person.PersonDb
import com.jpp.mpdata.datasources.search.SearchApi
import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdata.datasources.support.SupportDb
import com.jpp.mpdata.datasources.tokens.AccessTokenApi
import com.jpp.mpdata.preferences.LanguageDbImpl
import com.jpp.mpdata.preferences.SessionDbImpl
import com.jpp.mpdata.repository.about.AboutUrlRepositoryImpl
import com.jpp.mpdata.repository.account.AccountRepositoryImpl
import com.jpp.mpdata.repository.appversion.AppVersionRepositoryImpl
import com.jpp.mpdata.repository.configuration.ConfigurationRepositoryImpl
import com.jpp.mpdata.repository.connectivity.ConnectivityRepositoryImpl
import com.jpp.mpdata.repository.credits.CreditsRepositoryImpl
import com.jpp.mpdata.repository.genre.MovieGenreRepositoryImpl
import com.jpp.mpdata.repository.language.LanguageRepositoryImpl
import com.jpp.mpdata.repository.licenses.LicensesRepositoryImpl
import com.jpp.mpdata.repository.moviedetail.MovieDetailRepositoryImpl
import com.jpp.mpdata.repository.movies.MoviePageRepositoryImpl
import com.jpp.mpdata.repository.moviestate.MovieStateRepositoryImpl
import com.jpp.mpdata.repository.person.PersonRepositoryImpl
import com.jpp.mpdata.repository.search.SearchRepositoryImpl
import com.jpp.mpdata.repository.session.SessionRepositoryImpl
import com.jpp.mpdata.repository.support.SupportRepositoryImpl
import com.jpp.mpdata.repository.tokens.AccessTokenRepositoryImpl
import com.jpp.mpdomain.repository.AboutUrlRepository
import com.jpp.mpdomain.repository.AccessTokenRepository
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.AppVersionRepository
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
import com.jpp.mpdomain.repository.SupportRepository
import com.jpp.mpdomain.repository.MovieGenreRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides all dependencies for the data layer.
 */
@Module
class DataLayerModule {

    /*****************************************
     ****** COMMON INNER DEPENDENCIES ********
     *****************************************/

    @Singleton
    @Provides
    fun providesMPApi() = MPApi()

    @Provides
    @Singleton
    fun providesTheMoviesDBRoomDb(context: Context):
            MPRoomDataBase = Room
        .databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
        .fallbackToDestructiveMigration() // IMPORTANT: I don't want to keep user's data when going from v1 to v2. Keep in mind for future upgrades.
        .build()

    @Singleton
    @Provides
    fun providesRoomDomainAdapter() =
        RoomDomainAdapter()

    @Singleton
    @Provides
    fun providesDomainRoomAdapter() =
        DomainRoomAdapter()

    @Singleton
    @Provides
    fun providesCacheTimestampHelper() = CacheTimestampHelper()

    @Singleton
    @Provides
    fun providesConnectivityRepository(context: Context):
            ConnectivityRepository = ConnectivityRepositoryImpl(context)

    @Singleton
    @Provides
    fun providesLanguageMonitor(context: Context): LanguageMonitor = LanguageMonitor.Impl(context)

    /***********************************
     ****** MOVIES DEPENDENCIES ********
     ***********************************/

    @Singleton
    @Provides
    fun providesMoviesApi(mpApiInstance: MPApi): MoviePageApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMoviesDb(
        roomDb: MPRoomDataBase,
        toDomainAdapter: RoomDomainAdapter,
        toRoomAdapter: DomainRoomAdapter,
        timestampHelper: CacheTimestampHelper
    ): MoviePageDb = MoviesCache(roomDb, toDomainAdapter, toRoomAdapter, timestampHelper)

    @Singleton
    @Provides
    fun providesMoviesRepository(moviePageApi: MoviePageApi, moviePageDb: MoviePageDb):
            MoviePageRepository = MoviePageRepositoryImpl(moviePageApi, moviePageDb)

    /*****************************************
     ****** CONFIGURATION DEPENDENCIES *******
     *****************************************/

    @Singleton
    @Provides
    fun providesConfigurationApi(mpApiInstance: MPApi): ConfigurationApi = mpApiInstance

    @Singleton
    @Provides
    fun providesConfigurationDb(
        roomDb: MPRoomDataBase,
        toDomainAdapter: RoomDomainAdapter,
        toRoomAdapter: DomainRoomAdapter,
        timestampHelper: CacheTimestampHelper
    ): ConfigurationDb = ConfigurationCache(roomDb, toDomainAdapter, toRoomAdapter, timestampHelper)

    @Singleton
    @Provides
    fun providesConfigurationRepository(
        configurationApi: ConfigurationApi,
        configurationDb: ConfigurationDb
    ): ConfigurationRepository = ConfigurationRepositoryImpl(configurationApi, configurationDb)

    /**********************************
     ****** SEARCH DEPENDENCIES *******
     **********************************/
    @Singleton
    @Provides
    fun providesSearchApi(mpApiInstance: MPApi): SearchApi = mpApiInstance

    @Singleton
    @Provides
    fun providesSearchRepository(searchApi: SearchApi): SearchRepository =
        SearchRepositoryImpl(searchApi)

    /**********************************
     ****** PERSON DEPENDENCIES *******
     **********************************/

    @Singleton
    @Provides
    fun providesPersonApi(mpApiInstance: MPApi): PersonApi = mpApiInstance

    @Singleton
    @Provides
    fun providesPersonDb(): PersonDb = PersonDb.Impl()

    @Singleton
    @Provides
    fun providesPersonRepository(personApi: PersonApi, personDb: PersonDb):
            PersonRepository = PersonRepositoryImpl(personApi, personDb)

    /**********************************
     ****** CREDITS DEPENDENCIES ******
     **********************************/

    @Singleton
    @Provides
    fun providesCreditsApi(mpApiInstance: MPApi): CreditsApi = mpApiInstance

    @Singleton
    @Provides
    fun provideCreditsDb(
        roomDatabase: MPRoomDataBase,
        toDomainAdapter: RoomDomainAdapter,
        toRoomAdapter: DomainRoomAdapter,
        timestampHelper: CacheTimestampHelper
    ): CreditsDb = CreditsCache(roomDatabase, toDomainAdapter, toRoomAdapter, timestampHelper)

    @Singleton
    @Provides
    fun providesCreditsRepository(creditsApi: CreditsApi, creditsDb: CreditsDb):
            CreditsRepository = CreditsRepositoryImpl(creditsApi, creditsDb)

    /**************************************
     ****** APP VERSION DEPENDENCIES ******
     **************************************/

    @Singleton
    @Provides
    fun providesAppVersionRepository():
            AppVersionRepository = AppVersionRepositoryImpl()

    @Singleton
    @Provides
    fun providesAboutNavigationRepository(context: Context):
            AboutUrlRepository = AboutUrlRepositoryImpl(context)

    /**********************************
     ****** LICENSES DEPENDENCIES *****
     **********************************/

    @Singleton
    @Provides
    fun providesLicensesRepository(context: Context):
            LicensesRepository = LicensesRepositoryImpl(context)

    /**********************************
     ****** SUPPORT DEPENDENCIES ******
     **********************************/

    @Singleton
    @Provides
    fun providesLanguageDb(context: Context):
            LanguageDb = LanguageDbImpl(context)

    @Singleton
    @Provides
    fun providesLanguageRepository(languageDb: LanguageDb):
            LanguageRepository = LanguageRepositoryImpl(languageDb)

    @Singleton
    @Provides
    fun providesSupportDb(roomDatabase: MPRoomDataBase):
            SupportDb = SupportCache(roomDatabase)

    @Singleton
    @Provides
    fun providesSupportRepository(supportDb: SupportDb, personDb: PersonDb):
            SupportRepository = SupportRepositoryImpl(supportDb, personDb)

    /**********************************
     ****** SESSION DEPENDENCIES ******
     **********************************/

    @Singleton
    @Provides
    fun providesSessionApi(mpApiInstance: MPApi): SessionApi = mpApiInstance

    @Singleton
    @Provides
    fun providesSessionDb(context: Context): SessionDb = SessionDbImpl(context)

    @Singleton
    @Provides
    fun providesSessionRepository(sessionApi: SessionApi, sessionDb: SessionDb):
            SessionRepository = SessionRepositoryImpl(sessionApi, sessionDb)

    /**********************************
     ****** ACCOUNT DEPENDENCIES ******
     **********************************/

    @Singleton
    @Provides
    fun providesAccountApi(mpApiInstance: MPApi): AccountApi = mpApiInstance

    @Singleton
    @Provides
    fun providesAccountDb(): AccountDb = AccountDb.Impl()

    @Singleton
    @Provides
    fun providesAccountRepository(accountApi: AccountApi, accountDb: AccountDb): AccountRepository =
        AccountRepositoryImpl(accountApi, accountDb)

    /***************************************
     ****** ACCESS TOKEN DEPENDENCIES ******
     ***************************************/

    @Singleton
    @Provides
    fun providesAccessTokenApi(mpApiInstance: MPApi): AccessTokenApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMPAccessTokenRepository(accessTokenApi: AccessTokenApi):
            AccessTokenRepository = AccessTokenRepositoryImpl(accessTokenApi)

    /***************************************
     ****** MOVIE DETAIL DEPENDENCIES ******
     ***************************************/
    @Singleton
    @Provides
    fun providesMovieDetailApi(mpApiInstance: MPApi): MovieDetailApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMovieDetailDb(
        roomDb: MPRoomDataBase,
        toDomainAdapter: RoomDomainAdapter,
        toRoomAdapter: DomainRoomAdapter,
        timestampHelper: CacheTimestampHelper
    ): MovieDetailDb = MovieDetailCache(roomDb, toDomainAdapter, toRoomAdapter, timestampHelper)

    @Singleton
    @Provides
    fun providesMovieDetailRepository(
        movieDetailApi: MovieDetailApi,
        movieDetailDb: MovieDetailDb
    ): MovieDetailRepository = MovieDetailRepositoryImpl(movieDetailApi, movieDetailDb)

    @Singleton
    @Provides
    fun providesMovieStateApi(mpApiInstance: MPApi): MovieStateApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMovieStateRepository(movieStateApi: MovieStateApi): MovieStateRepository =
        MovieStateRepositoryImpl(movieStateApi)

    /***************************************
     ****** MOVIE GENRES DEPENDENCIES ******
     ***************************************/

    @Singleton
    @Provides
    fun providesMovieGenreApi(mpApiInstance: MPApi): MovieGenreApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMovieGenreDb(
        roomDb: MPRoomDataBase,
        toDomainAdapter: RoomDomainAdapter,
        toRoomAdapter: DomainRoomAdapter,
        timestampHelper: CacheTimestampHelper
    ): MovieGenreDb = MovieGenreCache(roomDb, toDomainAdapter, toRoomAdapter, timestampHelper)

    @Singleton
    @Provides
    fun providesMovieGenreRepository(
        movieGenreApi: MovieGenreApi,
        movieGenreDb: MovieGenreDb
    ): MovieGenreRepository = MovieGenreRepositoryImpl(movieGenreApi, movieGenreDb)
}
