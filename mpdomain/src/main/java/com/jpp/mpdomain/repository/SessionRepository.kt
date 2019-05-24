package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session

/**
 * Repository that handles all [Session] related data.
 */
interface SessionRepository {
    /**
     * @return the current [Session] being used - if any.
     */
    fun getCurrentSession(): Session?

    /**
     * Creates a new [Session] for the provided [AccessToken].
     */
    fun createSession(accessToken: AccessToken): Session?
}