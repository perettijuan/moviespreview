package com.jpp.mpaccount.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpaccount.account.UserAccountInteractor.UserAccountEvent.*
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Gravatar
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import javax.inject.Inject

class UserAccountInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                                private val sessionRepository: SessionRepository,
                                                private val accountRepository: AccountRepository) {


    sealed class UserAccountEvent {
        object UserNotLogged : UserAccountEvent()
        object NotConnectedToNetwork : UserAccountEvent()
        object UnknownError : UserAccountEvent()
        data class Success(val data: UserAccount) : UserAccountEvent()
    }


    private val _userAccountEvents by lazy { MutableLiveData<UserAccountEvent>() }

    val userAccountEvents: LiveData<UserAccountEvent> get() = _userAccountEvents

    fun fetchUserAccountData() {
        val session = sessionRepository.getCurrentSession()
        when (session) {
            null -> UserNotLogged
            else -> fetchAccountData(session)
        }.let {
            _userAccountEvents.postValue(it)
        }
    }

    private fun fetchAccountData(session: Session): UserAccountEvent {
        return accountRepository.getUserAccount(session)?.let {
            Success(mapAvatarUrl(it))
        } ?: when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> NotConnectedToNetwork
            is Connectivity.Connected -> UnknownError
        }
    }

    private fun mapAvatarUrl(userAccount: UserAccount): UserAccount {
        return userAccount.copy(
                avatar = userAccount.avatar.copy(
                        gravatar = userAccount.avatar.gravatar.copy(
                                hash = Gravatar.BASE_URL + userAccount.avatar.gravatar.hash + Gravatar.REDIRECT)
                )
        )
    }
}