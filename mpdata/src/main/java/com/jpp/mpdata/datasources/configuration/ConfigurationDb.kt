package com.jpp.mpdata.datasources.configuration

import com.jpp.mpdomain.AppConfiguration

/**
 * Database definition to manipulate all the [AppConfiguration] data locally.
 */
interface ConfigurationDb {
    /**
     * @return the unique [AppConfiguration] instance stored locally - if any.
     */
    fun getAppConfiguration(): AppConfiguration?

    /**
     * Saves the provided [appConfiguration] data locally.
     */
    fun saveAppConfiguration(appConfiguration: AppConfiguration)
}