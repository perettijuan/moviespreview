package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.common.extensions.and
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.datalayer.repository.configuration.ConfigurationCache
import com.jpp.moviespreview.domainlayer.AppConfiguration

class CacheConfigurationRepository(private val mpCache: MPTimestamps,
                                   private val mpDatabase: MPDataBase) : ConfigurationCache {

    override fun getConfiguration(): AppConfiguration? {
        return when (mpCache.isAppConfigurationUpToDate()) {
            true -> mpDatabase.getStoredAppConfiguration()
            false -> null
        }
    }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        mpDatabase.updateAppConfiguration(appConfiguration)
                .and { mpCache.updateAppConfigurationInserted() }
    }

}