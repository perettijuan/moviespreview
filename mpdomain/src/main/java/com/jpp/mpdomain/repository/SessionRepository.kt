package com.jpp.mpdomain.repository

/**
 * Repository that handles all session related information.
 */
interface SessionRepository {
    fun getSessionId(): String?
}