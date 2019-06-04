package com.jpp.mpdata.repository.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository

//TODO JPP functions commented here should go in a different repository when the favorite state in the details is implemented.
class AccountRepositoryImpl(private val accountApi: AccountApi,
                            private val accountDb: AccountDb) : AccountRepository {

    private val accountUpdates by lazy { MutableLiveData<UserAccount>() }

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

    //    override fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState? {
//        /*
//         * TODO JPP for the moment, we don't store this state in the local storage
//         * BUT it is a great candidate to store it and try to use the WorkManager
//         * to sync the state with the API
//         */
//        return accountApi.getMovieAccountState(movieId, session)
//    }

//    override fun updateMovieFavoriteState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean {
//        return accountApi
//                .updateMovieFavoriteState(movieId, asFavorite, userAccount, session)?.also {
//                    accountDb.flushData()
//                    dataUpdates.postValue(AccountRepository.AccountDataUpdate.FavoritesMovies)
//                } ?: false
//    }
//
//
//    override fun refresh() {
//        accountDb.flushData()
//    }
}