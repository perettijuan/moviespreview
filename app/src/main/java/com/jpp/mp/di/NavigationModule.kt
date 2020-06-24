package com.jpp.mp.di

import com.jpp.mp.main.MainNavigator
import com.jpp.mp.main.movies.MovieListNavigator
import com.jpp.mpabout.AboutNavigator
import com.jpp.mpaccount.account.UserAccountNavigator
import com.jpp.mpaccount.login.LoginNavigator
import com.jpp.mpcredits.CreditNavigator
import com.jpp.mpmoviedetails.MovieDetailsNavigator
import com.jpp.mpmoviedetails.rates.RateMovieNavigator
import com.jpp.mpsearch.SearchNavigator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides dependencies needed to handle the application's navigation.
 */
@Module
class NavigationModule {

    @Provides
    @Singleton
    fun providesMainNavigator(): MainNavigator = MainNavigator()

    @Provides
    fun providesMovieListNavigator(mainNavigator: MainNavigator): MovieListNavigator = mainNavigator

    @Provides
    fun providesMovieDetailsNavigator(mainNavigator: MainNavigator): MovieDetailsNavigator = mainNavigator

    @Provides
    fun providesRateMovieNavigator(mainNavigator: MainNavigator): RateMovieNavigator = mainNavigator

    @Provides
    fun providesCreditNavigator(mainNavigator: MainNavigator): CreditNavigator = mainNavigator

    @Provides
    fun providesSearchNavigator(mainNavigator: MainNavigator): SearchNavigator = mainNavigator

    @Provides
    fun providesAboutNavigator(mainNavigator: MainNavigator): AboutNavigator = mainNavigator

    @Provides
    fun providesLoginNavigator(mainNavigator: MainNavigator): LoginNavigator = mainNavigator

    @Provides
    fun providesUserAccountNavigator(mainNavigator: MainNavigator): UserAccountNavigator = mainNavigator
}