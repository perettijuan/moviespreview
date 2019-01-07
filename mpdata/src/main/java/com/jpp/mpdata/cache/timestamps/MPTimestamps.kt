package com.jpp.mpdata.cache.timestamps

/**
 * Represents the timestamps for each entity stored in the database. The cache system implemented
 * uses this timestamps to verify if the data stored is valid and/or it needs to be refreshed.
 */
interface MPTimestamps {
    /**
     * @return a boolean flag that indicates if the AppConfiguration stored is stored in the local
     * storage and if it is valid.
     */
    fun isAppConfigurationUpToDate(): Boolean

    /**
     * Called when the AppConfiguration is refreshed in the local storage of the device.
     */
    fun updateAppConfigurationInserted()

    /**
     * @return a boolean flag that indicates if the stored pages in the local storage are valid
     * or not.
     */
    fun isMoviePageUpToDate(): Boolean

    /**
     * Called each time a new page is stored in the local storage.
     */
    fun updateMoviePageInserted()
}