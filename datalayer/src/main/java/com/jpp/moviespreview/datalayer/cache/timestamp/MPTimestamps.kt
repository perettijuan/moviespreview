package com.jpp.moviespreview.datalayer.cache.timestamp

/**
 * Defines the signature of the timestamps that the cache stores locally.
 */
interface MPTimestamps {
    fun isAppConfigurationUpToDate(): Boolean
    fun updateAppConfigurationInserted()
    fun areMoviesUpToDate(): Boolean
    fun updateMoviesInserted()
}