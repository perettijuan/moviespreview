package com.jpp.mpdomain.repository

import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

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
