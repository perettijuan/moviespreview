package com.jpp.mpdata.repository.tokens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.tokens.AccessTokenApi
import com.jpp.mpdomain.repository.MPAccessTokenRepository
import com.jpp.mpdomain.repository.MPAccessTokenRepository.AccessTokenData

/**
 * [MPAccessTokenRepository] implementation to retrieve data related to [AccessToken]s.
 */
class MPAccessTokenRepositoryImpl(private val accessTokenApi: AccessTokenApi) : MPAccessTokenRepository {

    private val dataUpdates by lazy { MutableLiveData<AccessTokenData>() }

    override fun data(): LiveData<AccessTokenData> = dataUpdates

    override fun getAccessToken() {
        val data = accessTokenApi.getAccessToken()
        when {
            data == null -> AccessTokenData.NoAccessTokenAvailable
            !data.success -> AccessTokenData.NoAccessTokenAvailable
            else -> AccessTokenData.Success(data)
        }.let {
            dataUpdates.postValue(it)
        }
    }
}