package com.jpp.moviespreview.datalayer.db.repository

import com.jpp.moviespreview.datalayer.AppConfiguration
import com.jpp.moviespreview.datalayer.db.MoviesPreviewDataBase
import com.jpp.moviespreview.datalayer.db.cache.MPTimestamps
import com.jpp.moviespreview.datalayer.repository.ConfigurationRepository

class DBConfigurationRepository(private val mpCache: MPTimestamps,
                                private val mpDatabase: MoviesPreviewDataBase) : ConfigurationRepository {

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