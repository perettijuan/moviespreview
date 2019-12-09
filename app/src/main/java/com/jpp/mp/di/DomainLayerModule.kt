package com.jpp.mp.di

import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mpdomain.repository.ConfigurationRepository
import dagger.Module
import dagger.Provides

/**
 * Provides all dependencies for the domain layer.
 */
@Module
class DomainLayerModule {

    @Provides
    fun providesImagesPathInteractor(configurationRepository: ConfigurationRepository):
            ImagesPathInteractor = ImagesPathInteractor.Impl(configurationRepository)
}
