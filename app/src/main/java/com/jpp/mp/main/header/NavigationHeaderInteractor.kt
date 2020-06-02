package com.jpp.mp.main.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.main.header.NavigationHeaderInteractor.HeaderDataEvent.*
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.SessionRepository
import javax.inject.Inject

/**
 * Interactor to support the navigation header section of the main screen.
 * Takes care of accessing the repository layer to perform the inner state updates needed to
 * provide functionality in the header section of the navigation view.
 */
class NavigationHeaderInteractor @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val accountRepository: AccountRepository
) {

    /**
     * Represents the events that this interactor can route to the upper layers.
     */
    sealed class HeaderDataEvent {
        object UserNotLogged : HeaderDataEvent()
        object UnknownError : HeaderDataEvent()
        data class Success(val data: UserAccount) : HeaderDataEvent()
    }

    private val _events = MediatorLiveData<HeaderDataEvent>()

    init {
        _events.addSource(sessionRepository.sessionStateUpdates()) { session ->
            when (session) {
                null -> _events.postValue(UserNotLogged)
            }
        }

        _events.addSource(accountRepository.userAccountUpdates()) { account ->
            _events.postValue(Success(data = account))
        }
    }

    /**
     * @return a [LiveData] of [HeaderDataEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val userAccountEvents: LiveData<HeaderDataEvent> get() = _events

    /**
     * Retrieves the user account data from the repository layers.
     */
    fun getUserAccountData() {
        when (val session = sessionRepository.getCurrentSession()) {
            null -> UserNotLogged
            else -> retrieveUserAccountWhenUserLogged(session)
        }.let {
            _events.postValue(it)
        }
    }

    private fun retrieveUserAccountWhenUserLogged(session: Session): HeaderDataEvent {
        return accountRepository.getUserAccount(session)?.let {
            Success(data = it)
        } ?: UnknownError
    }
}
