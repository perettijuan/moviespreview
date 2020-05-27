package com.jpp.mp.di

import android.content.Context
import com.jpp.mp.MPApp
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Provides general purpose dependencies.
 */
@Module
class AppModule(private val appInstance: MPApp) {

    @Provides
    @Singleton
    fun providesContext(): Context = appInstance.applicationContext

    @Provides
    fun providesDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
