package com.jpp.moviespreview.datalayer.db.repository

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.db.MPDataBase
import com.jpp.moviespreview.datalayer.db.cache.MPCache
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository

class DBConfigurationRepository(private val mpCache: MPCache,
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