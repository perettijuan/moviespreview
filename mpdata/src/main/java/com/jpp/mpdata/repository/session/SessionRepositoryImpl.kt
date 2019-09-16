package com.jpp.mpdata.repository.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.SessionRepository

class SessionRepositoryImpl(private val sessionApi: SessionApi,
                            private val sessionDb: SessionDb) : SessionRepository {

    private val sessionUpdates = MutableLiveData<Session?>()

    override fun sessionStateUpdates(): LiveData<Session?> = sessionUpdates

    override fun getCurrentSession(): Session? = sessionDb.getSession()

    override fun createSession(accessToken: AccessToken): Session? {
        return sessionApi.createSession(accessToken)?.also {
            sessionDb.updateSession(it)
            sessionUpdates.postValue(it)
        }
    }

    override fun deleteCurrentSession() {
        sessionDb.flushData()
        sessionUpdates.postValue(null)
    }
}