package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * Repository definition to support [UserAccount] data.
 */
interface AccountRepository {
    /**
     * Retrieves a [UserAccount] for the provided [session].
     */
    fun getUserAccount(session: Session): UserAccount?
}