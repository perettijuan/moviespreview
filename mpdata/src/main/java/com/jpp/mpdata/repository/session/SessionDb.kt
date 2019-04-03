package com.jpp.mpdata.repository.session

import com.jpp.mpdomain.Session

interface SessionDb {
    fun getSession(): Session?
    fun updateSession(session: Session)
}