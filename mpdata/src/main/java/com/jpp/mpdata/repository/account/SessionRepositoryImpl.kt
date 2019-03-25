package com.jpp.mpdata.repository.account

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.repository.SessionRepository

class SessionRepositoryImpl(private val sessionApi: SessionApi) : SessionRepository {
    override fun getSessionId(): String? {
        return null
    }

    override fun getAccessToken(): AccessToken? {
        return sessionApi.getAccessToken()
    }
}