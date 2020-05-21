package com.jpp.mp.di

import android.content.Context
import com.jpp.mp.MPApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides general purpose dependencies.
 */
@Module
class AppModule(private val appInstance: MPApp) {

    @Provides
    @Singleton
    fun providesContext(): Context = appInstance.applicationContext
}
