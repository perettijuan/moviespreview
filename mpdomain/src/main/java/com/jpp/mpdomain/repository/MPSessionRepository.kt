package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session

/**
 * Repository definition used to handle all [Session] data.
 */
interface MPSessionRepository {
    /**
     * Encapsulates all possible data that this repository can handle.
     */
    sealed class SessionData {
        /*
         * Used when the repository can successfully retrieve the current
         * session being used.
         */
        data class CurrentSession(val data: Session) : SessionData()

        /*
         * Used when the repository can not retrieve the current session.
         * Usually, this indicates the user us not logged in/
         */
        object NoCurrentSessionAvailable : SessionData()

        /*
         * Used when a new session can be created by the repository.
         */
        data class SessionCreated(val data: Session) : SessionData()

        /*
         * Used when the repository fails to create a new session.
         */
        object UnableToCreateSession : SessionData()
    }

    /**
     * Subscribe to this LiveData object in order to get
     * notifications about the data that this repository can
     * handle.
     */
    fun data(): LiveData<SessionData>

    /**
     * Retrieves the current [Session] being used, if there's one. Usually, no
     * [Session] being used currently indicates that the user is not logged in yet.
     * It will post a new message to [data]: [SessionData.CurrentSession] if a
     * [Session] is being used; [SessionData.NoCurrentSessionAvailable] if there's no
     * [Session] available.
     */
    fun getCurrentSession()

    /**
     * Creates a new [Session] with the provided [AccessToken]. The [AccessToken]
     * needs to be already authorized by the user.
     * Posts a new message to [data]: [SessionData.SessionCreated] when the new
     * session is created. [SessionData.UnableToCreateSession] when the creation
     * fails for some reason.
     */
    fun createAndStoreSession(accessToken: AccessToken)
}