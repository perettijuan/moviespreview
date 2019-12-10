package com.jpp.mpdata.datasources.account

import com.jpp.mpdomain.UserAccount

/**
 * Database definition to manipulate all the [UserAccount] data locally.
 */
interface AccountDb {
    /**
     * Stores the provided [userAccount] data locally.
     */
    fun storeUserAccountInfo(userAccount: UserAccount)

    /**
     * @return the unique [UserAccount] data stored locally - if any.
     */
    fun getUserAccountInfo(): UserAccount?

    /**
     * Flushes out all locally stored data.
     */
    fun flushData()

    /**
     * Only in-memory caching since the info of the user should be refreshed every time
     * it is accessed.
     */
    class Impl : AccountDb {
        private var userAccountInfo: UserAccount? = null

        override fun storeUserAccountInfo(userAccount: UserAccount) {
            userAccountInfo = userAccount
        }

        override fun getUserAccountInfo(): UserAccount? = userAccountInfo

        override fun flushData() {
            userAccountInfo = null
        }
    }
}
