package com.jpp.mpdata.repository.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.SessionRepository

class SessionRepositoryImpl(
    private val sessionApi: SessionApi,
    private val sessionDb: SessionDb
) : SessionRepository {

    private val sessionUpdates = MutableLiveData<Session?>()

    override suspend fun sessionStateUpdates(): LiveData<Session?> = sessionUpdates

    override suspend fun getCurrentSession(): Session? = sessionDb.getSession()

    override suspend fun createSession(accessToken: AccessToken): Session? {
        return sessionApi.createSession(accessToken)?.also {
            sessionDb.updateSession(it)
            sessionUpdates.postValue(it)
        }
    }

    override suspend fun deleteCurrentSession() {
        sessionDb.flushData()
        sessionUpdates.postValue(null)
    }
}
