package com.jpp.mpdata.datasources.configuration

import com.jpp.mpdomain.AppConfiguration

/**
 * API definition to manipulate all the [AppConfiguration] data in the remote resources.
 */
interface ConfigurationApi {
    /**
     * @return the [AppConfiguration] retrieved from the remote resource. Null if an
     * error is detected in the process.
     */
    fun getAppConfiguration(): AppConfiguration?
}
