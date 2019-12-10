package com.jpp.mpdata.datasources.tokens

import com.jpp.mpdomain.AccessToken

/**
 * API definition to retrieve [AccessToken]s
 */
interface AccessTokenApi {
    /**
     * @return an [AccessToken] retrieved from the server when possible.
     * Null if an error occurs.
     */
    fun getAccessToken(): AccessToken?
}
