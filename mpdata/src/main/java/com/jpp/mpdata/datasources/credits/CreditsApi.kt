package com.jpp.mpdata.datasources.credits

import com.jpp.mpdomain.Credits

/**
 * API definition to retrieve all credits related data from the server.
 */
interface CreditsApi {
    /**
     * Retrieves the [Credits] that belongs to the provided [movieId] if any.
     * @return a [Credits] instance for the movie identified with [movieId] if one can
     * be found, null any other case.
     */
    fun getCreditsForMovie(movieId: Double): Credits?
}
