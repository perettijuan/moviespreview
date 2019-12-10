package com.jpp.mpdata.preferences

import android.content.Context
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.Session

/**
 * [SessionDb] implementation.
 * This implementation should store the session values more securely, but for simplicity
 * (and since the application is not widely used -yet-) I decided to just store the data
 * in a key-value database.
 */
class SessionDbImpl(private val context: Context) : SessionDb {

    private val preferences by lazy { context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE) }

    override fun getSession(): Session? {
        return preferences.getString(KEY_SESSION_STORED, null)?.let {
            Session(success = true, session_id = it)
        }
    }

    override fun updateSession(session: Session) {
        with(preferences.edit()) {
            putString(KEY_SESSION_STORED, session.session_id)
            apply()
        }
    }

    override fun flushData() {
        with(preferences.edit()) {
            putString(KEY_SESSION_STORED, null)
            apply()
        }
    }

    private companion object {
        const val PREFERENCES_FILE_NAME = "com.jpp.moviespreview.preferences.session"
        const val KEY_SESSION_STORED = "session_stored"
    }
}
