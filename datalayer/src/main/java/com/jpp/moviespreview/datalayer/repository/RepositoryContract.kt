package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.datalayer.AppConfiguration

/**
 * Repository definition to retrieve the [AppConfiguration] from
 * the data module.
 */
interface ConfigurationRepository {
    fun getConfiguration(): AppConfiguration?
    fun updateAppConfiguration(appConfiguration: AppConfiguration)
}