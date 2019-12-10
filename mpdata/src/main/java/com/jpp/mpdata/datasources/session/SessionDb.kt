package com.jpp.mpdata.datasources.session

import com.jpp.mpdomain.Session

/**
 * Database definition to manipulate all the [Session] data locally.
 */
interface SessionDb {

    /**
     * @return the unique [Session] data stored locally - if any.
     */
    fun getSession(): Session?

    /**
     * Updates the unique session data stored locally with the provided [session].
     */
    fun updateSession(session: Session)

    /**
     * Flushes out all locally stored data.
     */
    fun flushData()
}
