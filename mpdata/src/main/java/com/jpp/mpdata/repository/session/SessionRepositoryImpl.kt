package com.jpp.mpdata.repository.session

import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.SessionRepository
import kotlinx.coroutines.channels.Channel

class SessionRepositoryImpl(
    private val sessionApi: SessionApi,
    private val sessionDb: SessionDb
) : SessionRepository {

    private val sessionUpdates = Channel<Session?>()

    override suspend fun sessionStateUpdates(): Channel<Session?> = sessionUpdates

    override suspend fun getCurrentSession(): Session? = sessionDb.getSession()

    override suspend fun createSession(accessToken: AccessToken): Session? {
        return sessionApi.createSession(accessToken)?.also {
            sessionDb.updateSession(it)
            sessionUpdates.send(it)
        }
    }

    override suspend fun deleteCurrentSession() {
        sessionDb.flushData()
        sessionUpdates.send(null)
    }
}
