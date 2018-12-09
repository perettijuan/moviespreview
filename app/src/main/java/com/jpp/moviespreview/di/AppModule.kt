package com.jpp.moviespreview.di

import android.content.Context
import com.jpp.moviespreview.MPApp
import com.jpp.moviespreview.domainlayer.interactor.ConfigureMovieImagesInteractor
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageInteractor
import com.jpp.moviespreview.screens.main.movies.paging.MoviesPagingDataSourceFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides appModule scoped dependencies.
 */
@Module
class AppModule(private val appInstance: MPApp) {

    @Provides
    @Singleton
    fun providesContext(): Context = appInstance.applicationContext

    //TODO JPP -> this should live in a different module
    @Provides
    fun providesPagingDs(moviePageInteractor: GetMoviePageInteractor, configureMovieImagesInteractor: ConfigureMovieImagesInteractor)
            :  MoviesPagingDataSourceFactory = MoviesPagingDataSourceFactory(moviePageInteractor, configureMovieImagesInteractor)
}