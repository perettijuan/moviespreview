package com.jpp.mp.di

import com.jpp.mp.main.MainNavigator
import com.jpp.mp.main.movies.MovieListNavigator
import com.jpp.mpmoviedetails.MovieDetailsNavigator
import com.jpp.mpmoviedetails.rates.RateMovieNavigator
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
}