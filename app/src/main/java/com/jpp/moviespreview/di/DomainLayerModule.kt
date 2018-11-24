package com.jpp.moviespreview.di

import android.content.Context
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository
import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.ConnectivityVerifierImpl
import com.jpp.moviespreview.domainlayer.usecase.ConfigureApplicationUseCase
import com.jpp.moviespreview.domainlayer.usecase.configuration.ConfigureApplicationUseCaseImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Provides all dependencies for the domain layer.
 */
@Module
class DomainLayerModule {

    @Provides
    @Singleton
    fun providesConnectivityVerifier(context: Context): ConnectivityVerifier = ConnectivityVerifierImpl(context)


    @Provides
    @Singleton
    fun providesConfigureApplicationUseCase(configRepository: ConfigurationRepository,
                                            connectivityVerifier: ConnectivityVerifier): ConfigureApplicationUseCase = ConfigureApplicationUseCaseImpl(configRepository, connectivityVerifier)

}