package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session

/**
 * Repository that handles all session related information.
 */
interface SessionRepository {
    fun getSessionId(): String?

    fun getAccessToken(): AccessToken?
    fun getAuthenticationUrl(accessToken: AccessToken): String
    fun getAuthenticationRedirection(): String
    fun getSession(accessToken: AccessToken): Session?
}