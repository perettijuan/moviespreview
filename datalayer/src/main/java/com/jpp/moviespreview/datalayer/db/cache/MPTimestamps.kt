package com.jpp.moviespreview.datalayer.db.cache

/**
 * Defines the signature of the timestamps that the cache stores locally.
 */
interface MPTimestamps {
    fun isAppConfigurationUpToDate(): Boolean
    fun updateAppConfigurationInserted()
    fun areMoviesUpToDate(): Boolean
    fun updateMoviesInserted()
}