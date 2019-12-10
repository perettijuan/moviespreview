package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * Repository definition to support [UserAccount] data.
 */
interface AccountRepository {

    /**
     * Subscribe to this LiveData object when interested on getting updates
     * about [UserAccount] changes.
     */
    fun userAccountUpdates(): LiveData<UserAccount>

    /**
     * Retrieves a [UserAccount] for the provided [session].
     */
    fun getUserAccount(session: Session): UserAccount?

    /**
     * Flushes out any [UserAccount] data stored locally on the device.
     */
    fun flushUserAccountData()
}
