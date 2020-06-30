package com.jpp.mpdata.repository.account

import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository

/**
 * [AccountRepository] implementation.
 */
class AccountRepositoryImpl(
    private val accountApi: AccountApi,
    private val accountDb: AccountDb
) : AccountRepository {

    override suspend fun getUserAccount(session: Session): UserAccount? {
        return accountDb.getUserAccountInfo() ?: accountApi.getUserAccountInfo(session)?.also {
            accountDb.storeUserAccountInfo(it)
        }
    }

    override fun flushUserAccountData() {
        accountDb.flushData()
    }
}
