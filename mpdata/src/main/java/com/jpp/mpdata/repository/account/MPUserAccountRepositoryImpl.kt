package com.jpp.mpdata.repository.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.MPUserAccountRepository
import com.jpp.mpdomain.repository.MPUserAccountRepository.UserAccountData

/**
 * [MPUserAccountRepository] implementation. It verifies if the data is stored locally before fetching
 * new data from the server.
 */
class MPUserAccountRepositoryImpl(private val accountApi: AccountApi,
                                  private val accountDb: AccountDb) : MPUserAccountRepository {

    private val dataUpdates by lazy { MutableLiveData<UserAccountData>() }

    override fun data(): LiveData<UserAccountData> = dataUpdates

    override fun getUserAccount(session: Session) {
        val data = getAccountData(session)
        when (data) {
            null -> UserAccountData.NoUserAccountData
            else -> UserAccountData.Success(data)
        }.let {
            dataUpdates.postValue(it)
        }
    }

    private fun getAccountData(session: Session): UserAccount? {
        return accountDb.getUserAccountInfo() ?: run {
            accountApi.getUserAccountInfo(session)?.also {
                accountDb.storeUserAccountInfo(it)
            }
        }
    }
}