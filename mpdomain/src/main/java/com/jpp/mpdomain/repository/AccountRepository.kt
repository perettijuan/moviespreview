package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * Repository definition to support all information related to user accounts.
 */
interface AccountRepository {
    fun getUserAccount(session: Session): UserAccount?
}