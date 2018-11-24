package com.jpp.moviespreview.di

import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides all dependencies for the data layer.
 */
@Module
class DataLayerModule {

    @Singleton
    @Provides
    fun provideConfigurationRepository(): ConfigurationRepository = ConfigurationRepositoryImpl()
}