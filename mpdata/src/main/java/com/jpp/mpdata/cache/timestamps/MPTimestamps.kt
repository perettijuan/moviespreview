package com.jpp.mpdata.cache.timestamps

/**
 * Defines the signature of the timestamps that the cache stores locally to determinate
 * if the data stored is up-to-date or it should be updated.
 */
interface MPTimestamps {
    fun isMoviePageUpToDate(): Boolean
    fun updateMoviePageInserted()
}