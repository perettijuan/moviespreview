package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.AccessToken

/**
 * Repository definition used to handle all [AccessToken] data.
 */
interface MPAccessTokenRepository {
    /**
     * Encapsulates all possible data that this repository can handle.
     */
    sealed class AccessTokenData {
        /*
         * Used when the repository can successfully retrieve the data.
         */
        data class Success(val data: AccessToken) : AccessTokenData()
        /*
         * Used when there's no user account data available to retrieve.
         */
        object NoAccessTokenAvailable : AccessTokenData()
    }

    /**
     * Subscribe to this LiveData object in order to get
     * notifications about the data that this repository can
     * handle.
     */
    fun data(): LiveData<AccessTokenData>

    /**
     * Retrieves an [AccessToken] to be used in the login process.
     * It will post a new update to [data] when the process is done.
     */
    fun getAccessToken()
}