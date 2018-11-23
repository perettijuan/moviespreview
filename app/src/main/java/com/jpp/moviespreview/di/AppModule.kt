package com.jpp.moviespreview.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * TODO JPP -> for the moment, this class is to verify that everything compiles. Delete it if not needed!!!!
 */
@Module
class AppModule {


    @Provides
    @Singleton
    fun providesContext(context: Context): Context = context
}