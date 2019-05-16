package com.jpp.mpdata.repository.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.MPSessionRepository
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData

/**
 * [MPSessionRepository] implementation. It manages the session related data by pulling data
 * from the server when needed.
 */
class MPSessionRepositoryImpl(private val sessionApi: SessionApi,
                              private val sessionDb: SessionDb) : MPSessionRepository {

    private val dataUpdates by lazy { MutableLiveData<SessionData>() }

    override fun data(): LiveData<SessionData> = dataUpdates

    override fun getCurrentSession() {
        val storedSession = sessionDb.getSession()
        when (storedSession) {
            null -> SessionData.NoCurrentSessionAvailable
            else -> SessionData.CurrentSession(storedSession)
        }.let {
            dataUpdates.postValue(it)
        }
    }

    override fun createAndStoreSession(accessToken: AccessToken) {
        val createdSession = createAndStoreSessionImpl(accessToken)
        when (createdSession) {
            null -> SessionData.UnableToCreateSession
            else -> SessionData.SessionCreated(createdSession)
        }.let {
            dataUpdates.postValue(it)
        }
    }

    private fun createAndStoreSessionImpl(accessToken: AccessToken): Session? {
        return sessionApi.createSession(accessToken)?.also {
            sessionDb.updateSession(it)
        }
    }
}