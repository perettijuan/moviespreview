package com.jpp.mpdomain.usecase.support

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.AccountRepository.AccountDataUpdate.FavoritesMovies
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.LanguageRepository.LanguageEvent.LanguageChangeEvent

/**
 * Use case definition to update the data being handled by the application when needed.
 *
 * Some data that is stored in memory (or in local storage) needs to be updated under certain
 * circumstances. This use case takes care of monitoring the data sources that can trigger
 * this behavior and notifies to its clients when such situation is detected.
 */
interface RefreshAppDataUseCase {

    /**
     * Represents all application data refresh events that can be triggered when
     * the underlying data is updated.
     */
    sealed class AppDataRefresh {
        object UserAccountMovies : AppDataRefresh()
        object LanguageChanged : AppDataRefresh()
    }

    /**
     * Subscribe to this [LiveData] to be notified when the data in the application
     * needs to be refreshed for some reason.
     */
    fun appDataUpdates(): LiveData<AppDataRefresh>

    class Impl(private val accountRepository: AccountRepository,
               languageRepository: LanguageRepository) : RefreshAppDataUseCase {

        private val dataRefreshUpdates = MediatorLiveData<AppDataRefresh>()

        init {
            dataRefreshUpdates.addSource(accountRepository.updates()) { accountDataUpdate ->
                when (accountDataUpdate) {
                    is FavoritesMovies -> dataRefreshUpdates.postValue(AppDataRefresh.UserAccountMovies)
                }
            }
            dataRefreshUpdates.addSource(languageRepository.updates()) { languageRepositoryEvent ->
                when (languageRepositoryEvent) {
                    is LanguageChangeEvent -> {
                        accountRepository.refresh()
                        dataRefreshUpdates.postValue(AppDataRefresh.LanguageChanged)
                    }
                }
            }
        }

        override fun appDataUpdates(): LiveData<AppDataRefresh> = dataRefreshUpdates
    }
}