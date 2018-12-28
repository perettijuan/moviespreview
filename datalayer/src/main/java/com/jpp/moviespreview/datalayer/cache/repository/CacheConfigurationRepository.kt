package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.common.extensions.and
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import com.jpp.moviespreview.domainlayer.AppConfiguration
import com.jpp.moviespreview.domainlayer.repository.ConfigurationRepository

class CacheConfigurationRepository(private val mpCache: MPTimestamps,
                                   private val mpDatabase: MPDataBase) : ConfigurationRepository {

    override fun getConfiguration(): ConfigurationRepository.ConfigurationRepositoryOutput {
        return when (mpCache.isAppConfigurationUpToDate()) {
            true -> mpDatabase.getStoredAppConfiguration()
                    ?.let { ConfigurationRepository.ConfigurationRepositoryOutput.Success(it) }
                    ?: let { ConfigurationRepository.ConfigurationRepositoryOutput.Error }
            false -> ConfigurationRepository.ConfigurationRepositoryOutput.Error
        }
    }

    override fun updateAppConfiguration(appConfiguration: AppConfiguration) {
        mpDatabase.updateAppConfiguration(appConfiguration)
                .and { mpCache.updateAppConfigurationInserted() }
    }

}