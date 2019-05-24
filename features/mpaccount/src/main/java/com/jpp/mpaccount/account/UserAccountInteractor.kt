package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent.*
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import javax.inject.Inject

/**
 * Interactor to support the user account screen. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to provide functionality to the
 * user account screen.
 */
class UserAccountInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                                private val sessionRepository: SessionRepository,
                                                private val accountRepository: AccountRepository) {

    /**
     * Represents the events that this interactor can route to the upper layers.
     */
    sealed class UserAccountEvent {
        object UserNotLogged : UserAccountEvent()
        object NotConnectedToNetwork : UserAccountEvent()
        object UnknownError : UserAccountEvent()
        data class Success(val data: UserAccount) : UserAccountEvent()
    }

    private val _userAccountEvents by lazy { MutableLiveData<UserAccountEvent>() }

    /**
     * @return a [LiveData] of [UserAccountEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val userAccountEvents: LiveData<UserAccountEvent> get() = _userAccountEvents

    /**
     * Fetches the user account data to be shown in the screen.
     */
    fun fetchUserAccountData() {
        val session = sessionRepository.getCurrentSession()
        when (session) {
            null -> UserNotLogged
            else -> getUserAccount(session)
        }.let {
            _userAccountEvents.postValue(it)
        }
    }

    private fun getUserAccount(session: Session): UserAccountEvent {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> NotConnectedToNetwork
            is Connectivity.Connected -> accountRepository.getUserAccount(session)?.let {
                Success(it)
            } ?: UnknownError
        }
    }
}