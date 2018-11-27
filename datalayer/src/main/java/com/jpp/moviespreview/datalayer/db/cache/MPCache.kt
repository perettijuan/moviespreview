package com.jpp.moviespreview.datalayer.db.cache

/**
 * Defines the signature of the cache that MoviesPreview supports.
 */
interface MPCache {
    fun isAppConfigurationUpToDate(): Boolean
    fun updateAppConfigurationInserted()
}