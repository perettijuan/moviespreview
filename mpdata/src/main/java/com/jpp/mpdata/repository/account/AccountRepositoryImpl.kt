package com.jpp.mpdata.repository.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository

class AccountRepositoryImpl(private val accountApi: AccountApi,
                            private val accountDb: AccountDb) : AccountRepository {

    private val accountUpdates = MutableLiveData<UserAccount>()

    override fun userAccountUpdates(): LiveData<UserAccount> = accountUpdates

    override fun getUserAccount(session: Session): UserAccount? {
        return accountDb.getUserAccountInfo() ?: accountApi.getUserAccountInfo(session)?.also {
            accountDb.storeUserAccountInfo(it)
            accountUpdates.postValue(it)
        }
    }

    override fun flushUserAccountData() {
        accountDb.flushData()
    }
}