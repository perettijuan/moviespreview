package com.jpp.mp.di

import android.content.Context
import androidx.room.Room
import com.jpp.mpdata.api.MPApi
import com.jpp.mpdata.cache.*
import com.jpp.mpdata.cache.room.MPRoomDataBase
import com.jpp.mpdata.cache.room.RoomModelAdapter
import com.jpp.mpdata.preferences.LanguageDbImpl
import com.jpp.mpdata.preferences.SessionDbImpl
import com.jpp.mpdata.repository.about.AboutNavigationRepositoryImpl
import com.jpp.mpdata.repository.account.AccountApi
import com.jpp.mpdata.repository.account.AccountDb
import com.jpp.mpdata.repository.account.AccountRepositoryImpl
import com.jpp.mpdata.repository.session.SessionApi
import com.jpp.mpdata.repository.session.SessionDb
import com.jpp.mpdata.repository.session.SessionRepositoryImpl
import com.jpp.mpdata.repository.appversion.AppVersionRepositoryImpl
import com.jpp.mpdata.repository.configuration.ConfigurationApi
import com.jpp.mpdata.repository.configuration.ConfigurationDb
import com.jpp.mpdata.repository.configuration.ConfigurationRepositoryImpl
import com.jpp.mpdata.repository.connectivity.ConnectivityRepositoryImpl
import com.jpp.mpdata.repository.credits.CreditsApi
import com.jpp.mpdata.repository.credits.CreditsDb
import com.jpp.mpdata.repository.credits.CreditsRepositoryImpl
import com.jpp.mpdata.repository.licenses.LicensesRepositoryImpl
import com.jpp.mpdata.repository.movies.MoviesApi
import com.jpp.mpdata.repository.movies.MoviesDb
import com.jpp.mpdata.repository.movies.MoviesRepositoryImpl
import com.jpp.mpdata.repository.person.PersonApi
import com.jpp.mpdata.repository.person.PersonDb
import com.jpp.mpdata.repository.person.PersonRepositoryImpl
import com.jpp.mpdata.repository.search.SearchApi
import com.jpp.mpdata.repository.search.SearchRepositoryImpl
import com.jpp.mpdata.repository.support.LanguageDb
import com.jpp.mpdata.repository.support.LanguageRepositoryImpl
import com.jpp.mpdata.repository.support.SupportDb
import com.jpp.mpdata.repository.support.SupportRepositoryImpl
import com.jpp.mpdomain.repository.*
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
    fun providesTheMoviesDBRoomDb(context: Context)
            : MPRoomDataBase = Room
            .databaseBuilder(context, MPRoomDataBase::class.java, "MPRoomDataBase")
            .build()

    @Singleton
    @Provides
    fun providesRoomModelAdapter() = RoomModelAdapter()

    @Singleton
    @Provides
    fun providesCacheTimestampHelper() = CacheTimestampHelper()

    @Singleton
    @Provides
    fun providesConnectivityRepository(context: Context)
            : ConnectivityRepository = ConnectivityRepositoryImpl(context)


    /***********************************
     ****** MOVIES DEPENDENCIES ********
     ***********************************/

    @Singleton
    @Provides
    fun providesMoviesApi(mpApiInstance: MPApi): MoviesApi = mpApiInstance

    @Singleton
    @Provides
    fun providesMoviesDb(roomDb: MPRoomDataBase,
                         adapter: RoomModelAdapter,
                         timestampHelper: CacheTimestampHelper)
            : MoviesDb = MoviesCache(roomDb, adapter, timestampHelper)

    @Singleton
    @Provides
    fun providesMoviesRepository(moviesApi: MoviesApi,
                                 moviesDb: MoviesDb)
            : MoviesRepository = MoviesRepositoryImpl(moviesApi, moviesDb)

    /*****************************************
     ****** CONFIGURATION DEPENDENCIES *******
     *****************************************/

    @Singleton
    @Provides
    fun providesConfigurationApi(mpApiInstance: MPApi): ConfigurationApi = mpApiInstance

    @Singleton
    @Provides
    fun providesConfigurationDb(roomDb: MPRoomDataBase,
                                adapter: RoomModelAdapter,
                                timestampHelper: CacheTimestampHelper)
            : ConfigurationDb = ConfigurationCache(roomDb, adapter, timestampHelper)

    @Singleton
    @Provides
    fun providesConfigurationRepository(configurationApi: ConfigurationApi,
                                        configurationDb: ConfigurationDb)
            : ConfigurationRepository = ConfigurationRepositoryImpl(configurationApi, configurationDb)


    /**********************************
     ****** SEARCH DEPENDENCIES *******
     **********************************/
    @Singleton
    @Provides
    fun providesSearchApi(mpApiInstance: MPApi): SearchApi = mpApiInstance

    @Singleton
    @Provides
    fun providesSearchRepository(searchApi: SearchApi): SearchRepository = SearchRepositoryImpl(searchApi)

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
    fun providesPersonRepository(personApi: PersonApi,
                                 personDb: PersonDb)
            : PersonRepository = PersonRepositoryImpl(personApi, personDb)


    /**********************************
     ****** CREDITS DEPENDENCIES ******
     **********************************/

    @Singleton
    @Provides
    fun providesCreditsApi(mpApiInstance: MPApi): CreditsApi = mpApiInstance

    @Singleton
    @Provides
    fun provideCreditsDb(roomDatabase: MPRoomDataBase,
                         adapter: RoomModelAdapter,
                         timestampHelper: CacheTimestampHelper)
            : CreditsDb = CreditsCache(roomDatabase, adapter, timestampHelper)

    @Singleton
    @Provides
    fun providesCreditsRepository(creditsApi: CreditsApi,
                                  creditsDb: CreditsDb)
            : CreditsRepository = CreditsRepositoryImpl(creditsApi, creditsDb)

    /**************************************
     ****** APP VERSION DEPENDENCIES ******
     **************************************/

    @Singleton
    @Provides
    fun providesAppVersionRepository()
            : AppVersionRepository = AppVersionRepositoryImpl()

    @Singleton
    @Provides
    fun providesAboutNavigationRepository(context: Context)
            : AboutNavigationRepository = AboutNavigationRepositoryImpl(context)

    /**********************************
     ****** LICENSES DEPENDENCIES *****
     **********************************/

    @Singleton
    @Provides
    fun providesLicensesRepository(context: Context)
            : LicensesRepository = LicensesRepositoryImpl(context)


    /**********************************
     ****** SUPPORT DEPENDENCIES ******
     **********************************/

    @Singleton
    @Provides
    fun providesLanguageDb(context: Context)
            : LanguageDb = LanguageDbImpl(context)

    @Singleton
    @Provides
    fun providesLanguageRepository(languageDb: LanguageDb, context: Context)
            : LanguageRepository = LanguageRepositoryImpl(languageDb, context)

    @Singleton
    @Provides
    fun providesSupportDb(roomDatabase: MPRoomDataBase)
            : SupportDb = SupportCache(roomDatabase)

    @Singleton
    @Provides
    fun providesSupportRepository(supportDb: SupportDb, personDb: PersonDb)
            : SupportRepository = SupportRepositoryImpl(supportDb, personDb)


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
    fun providesSessionRepository(sessionApi: SessionApi, sessionDb: SessionDb)
            : SessionRepository = SessionRepositoryImpl(sessionApi, sessionDb)

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
    fun providesAccountRepository(accountApi: AccountApi, accountDb: AccountDb): AccountRepository = AccountRepositoryImpl(accountApi, accountDb)

}

