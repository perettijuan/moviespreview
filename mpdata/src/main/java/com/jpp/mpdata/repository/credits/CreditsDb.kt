package com.jpp.mpdata.repository.credits

import com.jpp.mpdomain.Credits

/**
 * Defines the signature for the database that stores entities related to movie credits.
 */
interface CreditsDb {
    /**
     * Retrieves a stored [Credits] that belongs to the provided [movieId] if any.
     * @return a [Credits] instance for the movie identified with [movieId] is there are
     * credits stored. If there aren't, null is returned.
     */
    fun getCreditsForMovie(movieId: Double): Credits?

    /**
     * Stores the provided [credits] in the local storage of the device.
     */
    fun storeCredits(credits: Credits)

    /**
     * Clear all stored data.
     */
    fun clearAllData()
}
