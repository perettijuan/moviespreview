package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session

/**
 * Repository that handles all [Session] related data.
 */
interface SessionRepository {

    /**
     * Subscribe to this LiveData object when interested on getting updates
     * about session creation/deletion.
     */
    fun sessionStateUpdates(): LiveData<Session?>

    /**
     * @return the current [Session] being used - if any.
     */
    fun getCurrentSession(): Session?

    /**
     * Deletes the current [Session] being used - if any.
     */
    fun deleteCurrentSession()

    /**
     * Creates a new [Session] for the provided [AccessToken].
     */
    fun createSession(accessToken: AccessToken): Session?
}