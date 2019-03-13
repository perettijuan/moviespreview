package com.jpp.mpdomain.repository

/**
 * Repository definition to fetch the current app version.
 */
interface AppVersionRepository {
    /**
     * @return a String object that represents the current version of the application.
     */
    fun getCurrentAppVersion(): String
}