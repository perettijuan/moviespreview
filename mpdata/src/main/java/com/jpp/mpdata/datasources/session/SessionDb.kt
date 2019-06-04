package com.jpp.mpdata.datasources.session

import com.jpp.mpdomain.Session

interface SessionDb {
    fun getSession(): Session?
    fun updateSession(session: Session)
    fun flushData()
}