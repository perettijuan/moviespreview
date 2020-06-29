package com.jpp.mpdata.repository.account

import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import kotlinx.coroutines.channels.Channel

/**
 * [AccountRepository] implementation.
 */
class AccountRepositoryImpl(
    private val accountApi: AccountApi,
    private val accountDb: AccountDb
) : AccountRepository {

    private val accountUpdates: Channel<UserAccount> = Channel()

    override fun userAccountUpdates(): Channel<UserAccount> = accountUpdates

    override suspend fun getUserAccount(session: Session): UserAccount? {
        return accountDb.getUserAccountInfo() ?: accountApi.getUserAccountInfo(session)?.also {
            accountDb.storeUserAccountInfo(it)
            accountUpdates.send(it)
        }
    }

    override fun flushUserAccountData() {
        accountDb.flushData()
    }
}
