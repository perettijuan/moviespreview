package com.jpp.mpdomain.repository

import androidx.lifecycle.LiveData
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount

/**
 * Repository definition used to handle all [UserAccount] data.
 */
interface MPUserAccountRepository {
    /**
     * Encapsulates all possible data that this repository can handle.
     */
    sealed class UserAccountData {
        /*
         * Used when the repository can successfully retrieve the data.
         */
        data class Success(val data: UserAccount) : UserAccountData()
        /*
         * Used when there's no user account data available to retrieve.
         */
        object NoUserAccountData : UserAccountData()
    }

    /**
     * Subscribe to this LiveData object in order to get
     * notifications about the data that this repository can
     * handle.
     */
    fun data(): LiveData<UserAccountData>

    /**
     * Retrieve the [UserAccount] related to the provided [session].
     * It will post a new [UserAccountData] object to [data].
     */
    fun getUserAccount(session: Session)
}