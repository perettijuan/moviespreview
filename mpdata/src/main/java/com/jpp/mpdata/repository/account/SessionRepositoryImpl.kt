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

    override fun getAuthenticationUrl(accessToken: String): String {
        return "$authUrl/$accessToken?redirect_to=$redirectUrl"
    }

    override fun getAuthenticationRedirection(): String {
       return redirectUrl
    }

    private companion object {
        const val authUrl = "https://www.themoviedb.org/authenticate/"
        const val redirectUrl = "http://www.mp.com/approved"
    }
}