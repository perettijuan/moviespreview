package com.jpp.mpdata.repository.account

import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository

//TODO JPP we need to do something with this
class AccountRepositoryImpl(private val accountApi: AccountApi,
                            private val accountDb: AccountDb) : AccountRepository {


    override fun getUserAccount(session: Session): UserAccount? {
        return accountDb.getUserAccountInfo() ?: run {
            accountApi.getUserAccountInfo(session)?.also {
                accountDb.storeUserAccountInfo(it)
            }
        }
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