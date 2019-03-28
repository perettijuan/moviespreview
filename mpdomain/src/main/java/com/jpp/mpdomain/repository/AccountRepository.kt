package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

interface AccountRepository {
    fun getUserAccount(session: Session): UserAccount?
}