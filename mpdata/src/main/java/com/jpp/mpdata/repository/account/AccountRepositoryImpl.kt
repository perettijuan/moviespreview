package com.jpp.mpdata.repository.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.AccountRepository

class AccountRepositoryImpl(private val accountApi: AccountApi,
                            private val accountDb: AccountDb) : AccountRepository {

    private val dataUpdates by lazy { MutableLiveData<AccountRepository.AccountDataUpdate>() }

    override fun updates(): LiveData<AccountRepository.AccountDataUpdate> = dataUpdates

    override fun getUserAccount(session: Session): UserAccount? {
        return accountDb.getUserAccountInfo() ?: run {
            accountApi.getUserAccountInfo(session)?.also {
                accountDb.storeUserAccountInfo(it)
            }
        }
    }

    override fun getMovieAccountState(movieId: Double, session: Session): MovieAccountState? {
        /*
         * TODO JPP for the moment, we don't store this state in the local storage
         * BUT it is a great candidate to store it and try to use the WorkManager
         * to sync the state with the API
         */
        return accountApi.getMovieAccountState(movieId, session)
    }

    override fun updateMovieFavoriteState(movieId: Double, asFavorite: Boolean, userAccount: UserAccount, session: Session): Boolean {
        return accountApi
                .updateMovieFavoriteState(movieId, asFavorite, userAccount, session)?.also {
                    accountDb.flushData()
                    dataUpdates.postValue(AccountRepository.AccountDataUpdate.FavoritesMovies)
                } ?: false
    }

    override fun getFavoriteMovies(page: Int, userAccount: UserAccount, session: Session, language: SupportedLanguage): MoviePage? {
        return accountDb.getFavoriteMovies(page) ?: run {
            accountApi.getFavoriteMovies(page, userAccount, session, language)?.also {
                accountDb.storeFavoriteMoviesPage(page, it)
            }
        }
    }

    override fun refresh() {
        accountDb.flushData()
    }
}