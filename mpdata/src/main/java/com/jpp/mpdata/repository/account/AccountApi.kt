package com.jpp.mpdata.repository.account

import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

interface AccountApi {
    fun getUserAccountInfo(session: Session): UserAccount?
}