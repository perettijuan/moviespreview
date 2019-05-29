package com.jpp.mpdata.repository.session

import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.SessionRepository

class SessionRepositoryImpl(private val sessionApi: SessionApi,
                            private val sessionDb: SessionDb) : SessionRepository {

    override fun getCurrentSession(): Session? = sessionDb.getSession()

    override fun createSession(accessToken: AccessToken): Session? {
        return sessionApi.createSession(accessToken)?.also {
            sessionDb.updateSession(it)
        }
    }

    override fun deleteCurrentSession() {
        sessionDb.flushData()
    }
}