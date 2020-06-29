package com.jpp.mp.di

import com.jpp.mp.main.Navigator
import com.jpp.mp.main.header.HeaderNavigator
import com.jpp.mp.main.movies.MovieListNavigator
import com.jpp.mpabout.AboutNavigator
import com.jpp.mpaccount.account.UserAccountNavigator
import com.jpp.mpaccount.account.lists.UserMovieListNavigator
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
    fun providesMainNavigator(): Navigator = Navigator()

    @Provides
    fun providesMovieListNavigator(navigator: Navigator): MovieListNavigator = navigator

    @Provides
    fun providesHeaderNavigator(navigator: Navigator): HeaderNavigator = navigator

    @Provides
    fun providesMovieDetailsNavigator(navigator: Navigator): MovieDetailsNavigator = navigator

    @Provides
    fun providesRateMovieNavigator(navigator: Navigator): RateMovieNavigator = navigator

    @Provides
    fun providesCreditNavigator(navigator: Navigator): CreditNavigator = navigator

    @Provides
    fun providesSearchNavigator(navigator: Navigator): SearchNavigator = navigator

    @Provides
    fun providesAboutNavigator(navigator: Navigator): AboutNavigator = navigator

    @Provides
    fun providesLoginNavigator(navigator: Navigator): LoginNavigator = navigator

    @Provides
    fun providesUserAccountNavigator(navigator: Navigator): UserAccountNavigator = navigator

    @Provides
    fun providesUserMovieListNavigator(navigator: Navigator): UserMovieListNavigator = navigator
}
