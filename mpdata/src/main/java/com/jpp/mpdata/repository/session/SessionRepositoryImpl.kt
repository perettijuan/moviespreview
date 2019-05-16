package com.jpp.mpdata.repository.session

import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.SessionRepository

class SessionRepositoryImpl(private val sessionApi: SessionApi,
                            private val sessionDb: SessionDb) : SessionRepository {

    override fun getCurrentSession(): Session? = sessionDb.getSession()
    override fun getAccessToken(): AccessToken? = sessionApi.getAccessToken()
    override fun getAuthenticationUrl(accessToken: AccessToken): String = "$authUrl/${accessToken.request_token}?redirect_to=$redirectUrl"
    override fun getAuthenticationRedirection(): String = redirectUrl

    override fun getSession(accessToken: AccessToken): Session? {
        return sessionApi.createSession(accessToken)?.also {
            sessionDb.updateSession(it)
        }
    }

    private companion object {
        const val authUrl = "https://www.themoviedb.org/authenticate/"
        const val redirectUrl = "http://www.mp.com/approved"
    }
}