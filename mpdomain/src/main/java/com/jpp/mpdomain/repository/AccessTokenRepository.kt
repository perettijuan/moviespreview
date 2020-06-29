package com.jpp.mpdomain.repository

import com.jpp.mpdomain.AccessToken

/**
 * Repository definition used to handle all [AccessToken] data.
 */
interface AccessTokenRepository {
    /**
     * Retrieves an [AccessToken] to be used in the login process.
     * It will post a new update to [data] when the process is done.
     */
    suspend fun getAccessToken(): AccessToken?
}
