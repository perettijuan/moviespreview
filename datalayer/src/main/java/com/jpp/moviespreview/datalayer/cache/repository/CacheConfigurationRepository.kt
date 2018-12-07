package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository

class CacheConfigurationRepository(private val mpCache: MPTimestamps,
                                   private val mpDatabase: MPDataBase) : ConfigurationRepository {

    override fun getConfiguration(): AppConfiguration? {
        return when (mpCache.isAppConfigurationUpToDate()) {
            true -> mpDatabase.getStoredAppConfiguration()
            else -> null
        }
    }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        mpDatabase
                .updateAppConfiguration(appConfiguration)
                .run {
                    mpCache.updateAppConfigurationInserted()
                }
    }

}