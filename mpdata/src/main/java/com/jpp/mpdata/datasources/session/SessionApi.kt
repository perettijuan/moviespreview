package com.jpp.mpdata.datasources.session

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session

/**
 * API definition to manipulate all the [Session] data in the remote resources.
 */
interface SessionApi {
    /**
     * Creates a new [Session] that will be related to the provided [accessToken].
     * @return the newly created [Session] or null when an error is detected.
     */
    fun createSession(accessToken: AccessToken): Session?
}
