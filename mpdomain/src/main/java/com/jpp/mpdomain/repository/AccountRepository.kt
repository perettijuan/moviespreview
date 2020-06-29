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
    suspend fun getUserAccount(session: Session): UserAccount?

    /**
     * Flushes out any [UserAccount] data stored locally on the device.
     */
    fun flushUserAccountData()
}
