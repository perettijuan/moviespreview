package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.datalayer.DataModelMapper
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.domainlayer.ImagesConfiguration
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class CacheConfigurationRepository(private val mpCache: MPTimestamps,
                                   private val mpDatabase: MPDataBase,
                                   private val mapper: DataModelMapper) : ConfigurationRepository {

    override fun getConfiguration(): ImagesConfiguration? {
        return when (mpCache.isAppConfigurationUpToDate()) {
            true -> mpDatabase.getStoredAppConfiguration()?.let { mapper.mapDataAppConfiguration(it) }
            false -> null
        }
    }

    override fun updateAppConfiguration(imagesConfiguration: ImagesConfiguration) {
        mapper.mapDomainImagesConfiguration(imagesConfiguration)
                .let { mpDatabase.updateAppConfiguration(it) }
                .run { mpCache.updateAppConfigurationInserted() }
    }

}