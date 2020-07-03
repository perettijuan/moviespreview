package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AppVersion

/**
 * Repository definition to fetch the current app version.
 */
interface AppVersionRepository {
    /**
     * @return the [AppVersion] that represents the current version of the application.
     */
    suspend fun getCurrentAppVersion(): AppVersion
}
