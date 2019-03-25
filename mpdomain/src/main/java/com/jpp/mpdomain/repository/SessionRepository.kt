package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AccessToken

/**
 * Repository that handles all session related information.
 */
interface SessionRepository {
    fun getSessionId(): String?

    /**
     * Retrieves an [AccessToken] that can be used to authenticate the user.
     */
    fun getAccessToken(): AccessToken?

    fun getAuthenticationUrl(accessToken: String): String
    fun getAuthenticationRedirection(): String
}