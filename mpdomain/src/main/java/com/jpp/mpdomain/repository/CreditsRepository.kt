package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Credits

/**
 * Repository definition to access all credits related data.
 */
interface CreditsRepository {
    /**
     * Retrieves the [Credits] of a movie identified by [movieId].
     * @return a [Credits] instance when credits for the movie can be found,
     * null any other case.
     */
    suspend fun getCreditsForMovie(movieId: Double): Credits?

    /**
     * Clear all data stored in the credits section.
     */
    suspend fun flushCreditsData()
}
