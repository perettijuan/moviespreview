package com.jpp.mpdata.repository.session

import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.SessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel

@ExperimentalCoroutinesApi
class SessionRepositoryImpl(
    private val sessionApi: SessionApi,
    private val sessionDb: SessionDb
) : SessionRepository {

    private val sessionUpdates = BroadcastChannel<Session?>(Channel.CONFLATED)

    override suspend fun sessionStateUpdates(): BroadcastChannel<Session?> = sessionUpdates

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
