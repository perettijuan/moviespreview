package com.jpp.mpdata.repository.tokens

import com.jpp.mpdata.datasources.tokens.AccessTokenApi
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.repository.AccessTokenRepository

/**
 * [AccessTokenRepository] implementation to retrieve data related to [AccessToken]s.
 */
class AccessTokenRepositoryImpl(private val accessTokenApi: AccessTokenApi) : AccessTokenRepository {
    override suspend fun getAccessToken(): AccessToken? = accessTokenApi.getAccessToken()
}
