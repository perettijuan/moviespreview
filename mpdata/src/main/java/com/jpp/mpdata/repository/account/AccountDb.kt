package com.jpp.mpdata.repository.account

import com.jpp.mpdomain.UserAccount

interface AccountDb {
    fun storeUserAccountInfo(userAccount: UserAccount)
    fun getUserAccountInfo(): UserAccount?

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
    }
}