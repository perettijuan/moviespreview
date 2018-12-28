package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.common.extensions.and
import com.jpp.moviespreview.datalayer.DataModelMapper
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class CacheConfigurationRepository(private val mpCache: MPTimestamps,
                                   private val mpDatabase: MPDataBase,
                                   private val mapper: DataModelMapper) : ConfigurationRepository {

    override fun getConfiguration(): ConfigurationRepository.ConfigurationRepositoryOutput {
        return when (mpCache.isAppConfigurationUpToDate()) {
            true -> mpDatabase.getStoredAppConfiguration()
                    ?.let { ConfigurationRepository.ConfigurationRepositoryOutput.Success(mapper.mapDataAppConfiguration(it)) }
                    ?: let { ConfigurationRepository.ConfigurationRepositoryOutput.Error }
            false -> ConfigurationRepository.ConfigurationRepositoryOutput.Error
        }
    }

    override fun updateAppConfiguration(imagesConfiguration: ImagesConfiguration) {
        mapper.mapDomainImagesConfiguration(imagesConfiguration)
                .let { mpDatabase.updateAppConfiguration(it) }
                .and { mpCache.updateAppConfigurationInserted() }
    }

}