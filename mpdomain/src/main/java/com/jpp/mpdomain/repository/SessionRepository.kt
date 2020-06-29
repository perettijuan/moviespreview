package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import kotlinx.coroutines.channels.Channel

/**
 * Repository that handles all [Session] related data.
 */
interface SessionRepository {

    /**
     * Subscribe to this LiveData object when interested on getting updates
     * about session creation/deletion.
     */
    suspend fun sessionStateUpdates(): Channel<Session?>

    /**
     * @return the current [Session] being used - if any.
     */
    suspend fun getCurrentSession(): Session?

    /**
     * Deletes the current [Session] being used - if any.
     */
    suspend fun deleteCurrentSession()

    /**
     * Creates a new [Session] for the provided [AccessToken].
     */
    suspend fun createSession(accessToken: AccessToken): Session?
}
