package com.jpp.mpdomain.repository.configuration

import com.jpp.mpdomain.AppConfiguration

/**
 * Repository definition to retrieve an [AppConfiguration] whenever is possible.
 */
interface ConfigurationRepository {
    /**
     * Retrieves the [AppConfiguration] that defines the current configuration of the
     * application.
     */
    fun getAppConfiguration(): AppConfiguration?
}